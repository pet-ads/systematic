package br.all.application.collaborator.find

import br.all.application.review.repository.CollaboratorRepository
import br.all.application.review.repository.CollaboratorTokenRepository
import br.all.application.shared.service.AuthorizationService
import br.all.domain.shared.user.Role

class FindAllCollaboratorsServiceImpl(
    private val collaboratorTokenRepository: CollaboratorTokenRepository,
    private val collaboratorRepository: CollaboratorRepository,
    private val authorizationService: AuthorizationService
): FindAllCollaboratorsService {
    override fun findAll(
        presenter: FindAllCollaboratorsPresenter,
        request: FindAllCollaboratorsService.Request
    ) {
        authorizationService.authorize(presenter, request.userId, request.systematicStudyId, setOf(Role.OWNER, Role.EDITOR, Role.VIEWER, Role.REVIEWER))
        if (presenter.isDone()) return

        val collaborators = collaboratorRepository.findAll(request.systematicStudyId).map { it.toCollaboratorOnReviewDto() }

        val invited = collaboratorTokenRepository.findAllBySystematicStudyId(request.systematicStudyId).map { it.toInvitedCollaboratorDto() }

        presenter.prepareSuccessView(FindAllCollaboratorsService.ResponseModel(invited, collaborators))
    }
}