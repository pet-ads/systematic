package br.all.application.reviewmode.assign

import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.shared.service.AuthorizationService
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.user.Role
import org.springframework.stereotype.Service

@Service
class AssignStudiesServiceImpl(
    private val reviewModeRepository: ReviewModeRepository,
    private val studyReviewRepository: StudyReviewRepository,
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

        val stageMode = when (request.stage) {
            StudyReviewStage.SELECTION -> reviewMode.selection
            StudyReviewStage.EXTRACTION -> reviewMode.extraction
        }
        if (stageMode == null) {
            presenter.prepareFailView(
                IllegalStateException("Review mode not configured for stage ${request.stage}")
            )
            return
        }

        val studies = studyReviewRepository.findAllFromReview(request.systematicStudyId)
        if (studies.isEmpty()) {
            presenter.prepareFailView(
                IllegalStateException("No studies to assign in systematic study ${request.systematicStudyId}")
            )
            return
        }
    }
}