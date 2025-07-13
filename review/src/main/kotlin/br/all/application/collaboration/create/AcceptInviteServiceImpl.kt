package br.all.application.collaboration.create

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfUnauthorized
import br.all.application.user.CredentialsService
import br.all.domain.model.collaboration.Collaboration
import br.all.domain.model.collaboration.CollaborationPermission
import br.all.domain.model.collaboration.toCollaborationId
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.toSystematicStudyId
import br.all.domain.model.user.toResearcherId
import java.util.UUID

class AcceptInviteServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
    private val collaborationRepository: CollaborationRepository
) : AcceptInviteService {
    override fun acceptInvite(presenter: AcceptInvitePresenter, request: AcceptInviteService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfUnauthorized(user)

        if(presenter.isDone()) return

        if(systematicStudy!!.collaborators.any { it.value() == request.userId }) {
            presenter.prepareFailView(
                IllegalArgumentException("User is already a collaborator")
            )
            return
        }
        
        val allInvites = collaborationRepository.listAllInvitesBySystematicStudyId(request.systematicStudyId)
        val userInvite = allInvites.firstOrNull { it.userId == request.userId } 
            
        if(userInvite == null) {
            presenter.prepareFailView(IllegalArgumentException("User has not been invited"))
            return
        }

        val collaborationId = UUID.randomUUID()
        val collaboration = Collaboration(
            collaborationId.toCollaborationId(),
            request.systematicStudyId.toSystematicStudyId(),
            request.userId.toResearcherId(),
            permissions = userInvite.permissions.map { CollaborationPermission.valueOf(it) }.toSet()
        )
        
        collaborationRepository.saveOrUpdateCollaboration(collaboration.toDto())
        presenter.prepareSuccessView(AcceptInviteService.ResponseModel(collaborationId))
    }
}