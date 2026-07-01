package br.all.review.requests

import br.all.application.review.create.InviteCollaboratorService.RequestModel
import java.util.*

data class InviteCollaboratorRequest(
    val usernameCollaborator: String
) {
    fun toCreateRequestModel(systematicStudyId: UUID, userId: UUID) =
        RequestModel(userId, systematicStudyId, usernameCollaborator)
}