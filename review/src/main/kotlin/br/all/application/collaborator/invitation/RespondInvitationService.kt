package br.all.application.collaborator.invitation

import br.all.application.collaborator.repository.InviteResponse
import java.util.*

interface RespondInvitationService {
    fun respond(presenter: ResponseInvitationPresenter, request: RequestModel)

    data class RequestModel(
        val token: UUID,
        val inviteResponse: InviteResponse,
    )

     class ResponseModel
}
