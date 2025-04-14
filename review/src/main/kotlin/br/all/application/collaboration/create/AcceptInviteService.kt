package br.all.application.collaboration.create

import java.util.*

interface AcceptInviteService {
    fun acceptInvite(presenter: AcceptInvitePresenter, request: RequestModel)

    data class RequestModel(
        val systematicStudyId: UUID,
        val userId: UUID,
        val inviteId: UUID,
    )

    data class ResponseModel(
        val collaborationId: UUID
    )
}