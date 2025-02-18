package br.all.application.search.delete

import br.all.application.search.update.PatchSearchSessionPresenter
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface DeleteSearchSessionService {
    fun delete(presenter: DeleteSearchSessionPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val sessionId: UUID,
    )

    @Schema(name = "DeleteSearchSessionServiceResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val sessionId: UUID,
    )
}