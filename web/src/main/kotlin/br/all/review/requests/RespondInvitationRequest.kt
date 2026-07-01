package br.all.review.requests

import br.all.application.review.create.RespondInvitationService.RequestModel
import br.all.application.review.repository.InviteResponse
import java.util.*

data class RespondInvitationRequest(
    val token: UUID,
    val inviteResponse: InviteResponse
) {
    fun toCreateRequestModel(token: UUID, inviteResponse: InviteResponse) =
        RequestModel(token, inviteResponse)
}