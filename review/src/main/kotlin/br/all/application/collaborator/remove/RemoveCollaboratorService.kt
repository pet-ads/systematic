package br.all.application.collaborator.remove

import java.util.*

interface RemoveCollaboratorService {
    fun remove(presenter: RemoveCollaboratorPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val researcherId: UUID,
    )

    class ResponseModel
}