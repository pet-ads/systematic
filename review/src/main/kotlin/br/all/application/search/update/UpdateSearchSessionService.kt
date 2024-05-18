package br.all.application.search.update

import java.time.LocalDateTime
import java.util.*

interface UpdateSearchSessionService {
    fun updateSession(presenter: UpdateSearchSessionPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val sessionId: UUID,
        val searchString: String?,
        val additionalInfo: String?,
        val source: String?
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val sessionId: UUID
    )
}