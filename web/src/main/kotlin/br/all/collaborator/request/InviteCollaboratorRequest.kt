package br.all.collaborator.request

import br.all.application.collaborator.invitation.InviteCollaboratorService.RequestModel
import java.util.*

data class InviteCollaboratorRequest(
    val usernameCollaborator: String
) {
    fun toCreateRequestModel(systematicStudyId: UUID, userId: UUID) =
        RequestModel(userId, systematicStudyId, usernameCollaborator)
}