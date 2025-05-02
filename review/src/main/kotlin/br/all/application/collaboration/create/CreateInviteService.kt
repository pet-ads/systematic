package br.all.application.collaboration.create

import java.util.*

interface CreateInviteService {
    fun createInvite(presenter: CreateInvitePresenter, request: RequestModel)
    
    data class RequestModel(
        val systematicStudyId: UUID,
        val userId: UUID,
        val inviteeId: UUID,
        val permissions: Set<String>
    )
    
    data class ResponseModel(
        val inviteId: UUID
    )
}