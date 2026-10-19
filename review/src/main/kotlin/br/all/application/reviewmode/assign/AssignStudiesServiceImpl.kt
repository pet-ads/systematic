package br.all.application.reviewmode.assign

import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.reviewmode.repository.ReviewerStudyDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.reviewmode.repository.toDomain
import br.all.application.shared.service.AuthorizationService
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.user.ResearcherId
import br.all.domain.shared.user.Role
import org.springframework.stereotype.Service

@Service
class AssignStudiesServiceImpl(
    private val reviewModeRepository: ReviewModeRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val reviewerStudyRepository: ReviewerStudyRepository,
    private val authorizationService: AuthorizationService,
) : AssignStudiesService {

    override fun assign(presenter: AssignStudiesPresenter, request: AssignStudiesService.RequestModel) {
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

        val stageMode = stageModeDto.toDomain()

        val studies = studyReviewRepository.findAllFromReview(request.systematicStudyId)
        if (studies.isEmpty()) {
            presenter.prepareFailView(
                IllegalStateException("No studies to assign in systematic study ${request.systematicStudyId}")
            )
            return
        }

        val reviewerStudies = reviewerStudyRepository
            .findAllBySystematicStudyIdAndStage(request.systematicStudyId, request.stage)

        val alreadyAssigned = reviewerStudies.map { it.studyId }.toSet()
        val availableStudies = studies.map { it.studyReviewId }.filter { it !in alreadyAssigned }
        if (availableStudies.isEmpty()) {
            presenter.prepareFailView(
                IllegalStateException("All studies are already assigned for stage ${request.stage}")
            )
            return
        }


        val studiesByReviewer = reviewerStudies
            .groupBy({ ResearcherId(it.researcherId) }, { it.studyId })
            .mapValues { it.value.toSet() }

        val originals = studies.associateBy { it.studyReviewId }
        val newReviewerStudies = stageMode.assign(availableStudies, studiesByReviewer)
            .flatMap { (reviewer, studyIds) ->
                studyIds.map { studyId ->
                    ReviewerStudyDto(
                        studyId = studyId,
                        systematicStudyId = request.systematicStudyId,
                        researcherId = reviewer.value,
                        stage = request.stage,
                        studyReview = originals.getValue(studyId),
                    )
                }
            }

        reviewerStudyRepository.saveAll(newReviewerStudies)
        presenter.prepareSuccessView(
            AssignStudiesService.ResponseModel()
        )
    }
}