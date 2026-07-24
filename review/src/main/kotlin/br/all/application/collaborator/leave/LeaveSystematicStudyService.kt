package br.all.application.collaborator.leave

import java.util.*

interface LeaveSystematicStudyService {
    fun leave(presenter: LeaveSystematicStudyPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID
    )

    class ResponseModel
}