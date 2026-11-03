package br.all.application.study.find.service

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.application.study.find.service.FindReviewerStudyService.RequestModel
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UnauthorizedUserException

class FindReviewerStudyServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val reviewerStudyRepository: ReviewerStudyRepository,
    private val credentialsService: CredentialsService,
) : FindReviewerStudyService {

    override fun findOne(presenter: FindStudyReviewPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        if (request.reviewerId != request.userId) {
            presenter.prepareFailView(
                UnauthorizedUserException("A reviewer can only access the studies assigned to himself.")
            )
            return
        }

        val copy = reviewerStudyRepository.findById(
            request.systematicStudyId, request.reviewerId, request.studyReviewId, request.stage
        )
        if (copy == null) {
            presenter.prepareFailView(
                EntityNotFoundException(
                    "Study ${request.studyReviewId} is not assigned to reviewer ${request.reviewerId} in stage ${request.stage}."
                )
            )
            return
        }

        presenter.prepareSuccessView(FindStudyReviewService.ResponseModel(request.userId, copy.studyReview))
    }
}