@file:Suppress("ktlint:standard:no-wildcard-imports")

package br.all.application.search.delete

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface DeleteSearchSessionService {
    fun delete(
        presenter: DeleteSearchSessionPresenter,
        request: RequestModel,
    )

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
