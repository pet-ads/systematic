package br.all.application.reviewmode.define

import br.all.application.reviewmode.define.DefineReviewModeService.RequestModel
import br.all.application.reviewmode.define.DefineReviewModeService.ResponseModel
import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.reviewmode.repository.toDomain
import br.all.application.reviewmode.repository.toDto
import br.all.application.shared.service.AuthorizationService
import br.all.domain.shared.user.Role
import org.springframework.stereotype.Service

@Service
class DefineReviewModeServiceImpl(
    private val reviewModeRepository: ReviewModeRepository,
    private val authorizationService: AuthorizationService
) : DefineReviewModeService {

    override fun define(presenter: DefineReviewModePresenter, request: RequestModel) {
        if (authorizationService.authorize(
                presenter,
                request.userId,
                request.systematicStudyId,
                setOf(Role.OWNER)
            ) == null
        ) return

        val reviewMode = reviewModeRepository.findById(request.systematicStudyId)?.toDomain()

        reviewMode?.defineMode(request.stage, request.toReviewMode())
        reviewMode?.let { reviewModeRepository.saveOrUpdate(it.toDto()) }

        presenter.prepareSuccessView(
            ResponseModel(request.userId, request.systematicStudyId, request.stage)
        )
    }
}
