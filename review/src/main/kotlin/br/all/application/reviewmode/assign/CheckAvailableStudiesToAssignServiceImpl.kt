package br.all.application.reviewmode.assign

import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.shared.service.AuthorizationService
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.user.Role
import org.springframework.stereotype.Service

@Service
class CheckAvailableStudiesToAssignServiceImpl(
    private val reviewModeRepository: ReviewModeRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val reviewerStudyRepository: ReviewerStudyRepository,
    private val authorizationService: AuthorizationService,
) : CheckAvailableStudiesToAssignService {

    override fun check(
        presenter: CheckAvailableStudiesToAssignPresenter,
        request: CheckAvailableStudiesToAssignService.RequestModel,
    ) {
        if (authorizationService.authorize(
                presenter,
                request.userId,
                request.systematicStudyId,
                setOf(Role.OWNER)
            ) == null
        ) return

        val reviewMode = reviewModeRepository.findById(request.systematicStudyId)
        if (reviewMode == null) {
            presenter.prepareFailView(
                EntityNotFoundException("Review mode of systematic study ${request.systematicStudyId} not found")
            )
            return
        }

        val stageModeDto = when (request.stage) {
            StudyReviewStage.SELECTION -> reviewMode.selection
            StudyReviewStage.EXTRACTION -> reviewMode.extraction
        }

        if (stageModeDto == null) {
            presenter.prepareFailView(
                IllegalStateException("Review mode not configured for stage ${request.stage}")
            )
            return
        }

        val studies = when (request.stage) {
            StudyReviewStage.SELECTION -> studyReviewRepository.findAllFromReview(request.systematicStudyId)
            StudyReviewStage.EXTRACTION -> studyReviewRepository.findAllIncludedInSelection(request.systematicStudyId)
        }.filterNot { statusOf(it, request.stage) == "DUPLICATED" }

        if (studies.isEmpty()) {
            presenter.prepareSuccessView(
                CheckAvailableStudiesToAssignService.ResponseModel(hasAvailableStudies = false)
            )
            return
        }

        val reviewerStudies = reviewerStudyRepository.findAllByIdSystematicStudyIdAndIdStage(
            request.systematicStudyId,
            request.stage
        )

        val alreadyAssigned = reviewerStudies.map { it.studyId }.toSet()
        val hasAvailableStudies = studies.any { it.studyReviewId !in alreadyAssigned }

        presenter.prepareSuccessView(
            CheckAvailableStudiesToAssignService.ResponseModel(hasAvailableStudies)
        )
    }

    private fun statusOf(study: StudyReviewDto, stage: StudyReviewStage) = when (stage) {
        StudyReviewStage.SELECTION -> study.selectionStatus
        StudyReviewStage.EXTRACTION -> study.extractionStatus
    }
}
