package br.all.application.search.create

import java.util.*

interface CreateSearchSessionService {
    fun createSession(presenter: CreateSearchSessionPresenter, request: RequestModel, file: String)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val source: String,
        val searchString: String,
        val additionalInfo: String?,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val sessionId: UUID,
    )
}