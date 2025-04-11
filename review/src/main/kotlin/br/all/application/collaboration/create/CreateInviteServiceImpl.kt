package br.all.application.collaboration.create

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
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

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if(presenter.isDone()) return
        
        if(systematicStudy!!.collaborators.any { it.value() == request.inviteeId }) {
            presenter.prepareFailView(
                IllegalArgumentException("User who is already a collaborator may not be invited")
            )
        }
        
        val inviteId = UUID.randomUUID()
        val invite = Invite(
            inviteId.toInviteId(),
            request.systematicStudyId.toSystematicStudyId(),
            request.userId.toResearcherId(),
            LocalDateTime.now()
        )
        
        collaborationRepository.saveOrUpdateInvite(invite.toDto())
        
        presenter.prepareSuccessView(CreateInviteService.ResponseModel(inviteId))
    }
}