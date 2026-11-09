package br.all.application.reviewmode.define

import br.all.application.collaborator.repository.CollaboratorRepository
import br.all.application.reviewmode.define.DefineReviewModeService.RequestModel
import br.all.application.reviewmode.define.DefineReviewModeService.ResponseModel
import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.reviewmode.repository.toDomain
import br.all.application.reviewmode.repository.toDto
import br.all.application.shared.service.AuthorizationService
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.reviewmode.SystematicStudyReviewMode
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.user.Role
import org.springframework.stereotype.Service

@Service
class DefineReviewModeServiceImpl(
    private val reviewModeRepository: ReviewModeRepository,
    private val authorizationService: AuthorizationService,
    private val collaboratorRepository: CollaboratorRepository
) : DefineReviewModeService {

    override fun define(presenter: DefineReviewModePresenter, request: RequestModel) {
        if (authorizationService.authorize(
                presenter,
                request.userId,
                request.systematicStudyId,
                setOf(Role.OWNER)
            ) == null
        ) return

        val collaboratorIds = collaboratorRepository
            .findAll(request.systematicStudyId)
            .map { it.researcherId }
            .toSet()

        val unknownReviewers = request.reviewerIds() - collaboratorIds - request.userId
        if (unknownReviewers.isNotEmpty()) {
            presenter.prepareFailView(
                EntityNotFoundException("Reviewers are not collaborators of the systematic study: $unknownReviewers")
            )
            return
        }

        val reviewMode = reviewModeRepository.findById(request.systematicStudyId)?.toDomain()
            ?: SystematicStudyReviewMode(SystematicStudyId(request.systematicStudyId))

        reviewMode.defineMode(request.stage, request.toReviewMode())
        reviewModeRepository.saveOrUpdate(reviewMode.toDto())

        presenter.prepareSuccessView(
            ResponseModel(request.userId, request.systematicStudyId, request.stage)
        )
    }
}
