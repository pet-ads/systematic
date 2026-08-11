package br.all.collaborator.request

import br.all.application.collaborator.invitation.RespondInvitationService.RequestModel
import br.all.application.collaborator.repository.InviteResponse
import java.util.*

data class RespondInvitationRequest(
    val token: UUID,
    val inviteResponse: InviteResponse
) {
    fun toCreateRequestModel(token: UUID, inviteResponse: InviteResponse) =
        RequestModel(token, inviteResponse)
}