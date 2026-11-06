package br.all.application.study.find.service

import br.all.application.agreement.visibility.AgreementVisibilityPolicy
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.shared.service.AuthorizationService
import br.all.application.study.find.presenter.FindAllStudyReviewsByReviewerPresenter
import br.all.application.study.find.service.FindAllStudyReviewsByReviewerService.RequestModel
import br.all.application.study.find.service.FindAllStudyReviewsByReviewerService.ResponseModel
import br.all.domain.shared.exception.UnauthorizedUserException
import br.all.domain.shared.user.Role
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class FindAllStudyReviewsByReviewerServiceImpl(
    private val authorizationService: AuthorizationService,
    private val reviewerStudyRepository: ReviewerStudyRepository,
    private val agreementVisibilityPolicy: AgreementVisibilityPolicy,
) : FindAllStudyReviewsByReviewerService {

    override fun findAllByReviewer(presenter: FindAllStudyReviewsByReviewerPresenter, request: RequestModel) {
        val context = authorizationService.authorize(
            presenter, request.userId, request.systematicStudyId, Role.entries.toSet()
        ) ?: return

        if (request.reviewerId != request.userId) {
            val stage = request.stage
            if (stage == null) {
                presenter.prepareFailView(
                    IllegalArgumentException("The stage is required to see another reviewer's studies.")
                )
                return
            }

            val decision = agreementVisibilityPolicy.evaluate(
                request.userId, request.systematicStudyId, stage, context.role
            )
            if (!decision.allowed) {
                presenter.prepareFailView(
                    UnauthorizedUserException(
                        "The agreement visibility of ${stage.name} does not allow seeing another reviewer's studies."
                    )
                )
                return
            }
        }

        val pageable = PageRequest.of(request.page, request.pageSize, parseSortParameter(request.sort))
        val reviewerStudies = reviewerStudyRepository.findAllByReviewerPaged(
            request.systematicStudyId, request.reviewerId, request.stage, pageable
        )

        presenter.prepareSuccessView(
            ResponseModel(
                userId = request.userId,
                systematicStudyId = request.systematicStudyId,
                reviewerId = request.reviewerId,
                stage = request.stage,
                studyReviews = reviewerStudies.content.map { it.studyReview },
                page = pageable.pageNumber,
                size = pageable.pageSize,
                totalElements = reviewerStudies.totalElements,
                totalPages = reviewerStudies.totalPages
            )
        )
    }

    private fun parseSortParameter(sortParam: String): Sort {
        val parts = sortParam.split(",")
        val direction = if (parts.size > 1 && parts[1].equals("desc", ignoreCase = true)) {
            Sort.Direction.DESC
        } else {
            Sort.Direction.ASC
        }
        return Sort.by(direction, parts[0])
    }
}