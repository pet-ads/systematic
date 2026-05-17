package br.all.application.review.create

import br.all.application.user.repository.TokenStatus
import java.util.*

interface InviteCollaboratorService {
    fun create(presenter: InviteCollaboratorPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val usernameCollaborator: String
    )

    data class ResponseModel(
        val collaboratorUsername: String,
        val collaboratorEmail: String,
        val inviteStatus: TokenStatus,
    )
}
