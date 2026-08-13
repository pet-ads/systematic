package br.all.application.collaborator.remove

import java.util.*

interface RemoveCollaboratorInviteService {
    fun remove(presenter: RemoveCollaboratorInvitePresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val researcherId: UUID,
    )

    class ResponseModel
}