package br.all.application.search.update

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface PatchSearchSessionService {
    fun patchSession(presenter: PatchSearchSessionPresenter, request: RequestModel, file: String)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val sessionId: UUID,
    )

    @Schema(name = "PatchSearchSessionServiceResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val sessionId: UUID
    )
}