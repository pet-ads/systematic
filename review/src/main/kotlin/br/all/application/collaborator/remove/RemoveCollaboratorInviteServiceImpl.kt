package br.all.application.collaborator.remove

import br.all.application.collaborator.repository.CollaboratorTokenRepository
import br.all.application.shared.service.AuthorizationService
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.user.Role
import org.springframework.transaction.annotation.Transactional

@Transactional
class RemoveCollaboratorInviteServiceImpl(
    private val collaboratorTokenRepository: CollaboratorTokenRepository,
    private val authorizationService: AuthorizationService
): RemoveCollaboratorInviteService {
    override fun remove(
        presenter: RemoveCollaboratorInvitePresenter,
        request: RemoveCollaboratorInviteService.RequestModel
    ) {
        authorizationService.authorize(presenter, request.userId, request.systematicStudyId, setOf(Role.OWNER))
        if (presenter.isDone()) return


        val collaboratorToken = collaboratorTokenRepository.findBySystematicStudyIdAndResearcherId(request.systematicStudyId, request.researcherId)

        if(collaboratorToken == null) {
            presenter.prepareFailView(EntityNotFoundException("There is no active invitation for researcher ${request.researcherId} in this systematic study."))
            return
        }

        collaboratorTokenRepository.deleteById(collaboratorToken.id)

        presenter.prepareSuccessView(RemoveCollaboratorInviteService.ResponseModel())
    }
}