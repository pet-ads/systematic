package br.all.application.collaboration.create

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.collaboration.repository.toDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.collaboration.CollaborationPermission
import br.all.domain.model.collaboration.Invite
import br.all.domain.model.collaboration.toInviteId
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.toSystematicStudyId
import br.all.domain.model.user.toResearcherId
import java.time.LocalDateTime
import java.util.*

class CreateInviteServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
    private val collaborationRepository: CollaborationRepository
) : CreateInviteService 
{
    override fun createInvite(presenter: CreateInvitePresenter, request: CreateInviteService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)

        if(presenter.isDone()) return
        
        if(systematicStudy!!.collaborators.any { it.value() == request.inviteeId }) {
            presenter.prepareFailView(
                IllegalArgumentException("User who is already a collaborator may not be invited")
            )
        }
        
        val allInvites = collaborationRepository.listAllInvitesBySystematicStudyId(request.systematicStudyId)
        if(allInvites.any { it.userId == request.inviteeId }) {
            presenter.prepareFailView(
                IllegalArgumentException("User who is already invited may not be invited again")
            )
        }
        
        val inviteId = UUID.randomUUID()
        val invite = Invite(
            inviteId.toInviteId(),
            request.systematicStudyId.toSystematicStudyId(),
            request.userId.toResearcherId(),
            request.permissions.map { CollaborationPermission.valueOf(it) }.toSet(),
            inviteDate = LocalDateTime.now(),
        )
        
        collaborationRepository.saveOrUpdateInvite(invite.toDto())
        
        presenter.prepareSuccessView(CreateInviteService.ResponseModel(inviteId))
    }
}