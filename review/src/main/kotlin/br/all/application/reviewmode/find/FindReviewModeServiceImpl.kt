package br.all.application.reviewmode.find

import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.reviewmode.repository.toDomain
import br.all.application.shared.service.AuthorizationService
import br.all.domain.shared.user.Role
import org.springframework.stereotype.Service

@Service
class FindReviewModeServiceImpl(
    private val authorizationService: AuthorizationService,
    private val reviewModeRepository: ReviewModeRepository,
) : FindReviewModeService {

    override fun find(presenter: FindReviewModePresenter, request: FindReviewModeService.RequestModel) {
        authorizationService.authorize(
            presenter, request.userId, request.systematicStudyId, Role.entries.toSet()
        ) ?: return

        val reviewMode = checkNotNull(reviewModeRepository.findById(request.systematicStudyId)?.toDomain()) {
            "Review mode of systematic study ${request.systematicStudyId} should exist"
        }

        presenter.prepareSuccessView(
            FindReviewModeService.ResponseModel(request.systematicStudyId, reviewMode.selection, reviewMode.extraction)
        )
    }
}