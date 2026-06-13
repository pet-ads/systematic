package br.all.application.review.create

import br.all.application.review.repository.InviteResponse
import java.util.*

interface RespondInvitationService {
    fun create(presenter: ResponseInvitationPresenter, request: RequestModel)

    data class RequestModel(
        val token: UUID,
        val inviteResponse: InviteResponse,
    )

     class ResponseModel
}
