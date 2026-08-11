package br.all.application.collaborator.update

import java.util.*

interface PassOwnershipService {
    fun pass(presenter: PassOwnershipPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val newOwnerId: UUID,
    )

    data class ResponseModel(
        val systematicStudyId: UUID,
        val newOwnerId: UUID,
    )
}