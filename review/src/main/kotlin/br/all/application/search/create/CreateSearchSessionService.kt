package br.all.application.search.create

import io.swagger.v3.oas.annotations.media.Schema
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

    @Schema(name = "CreateSearchSessionServiceResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val sessionId: UUID,
    )
}