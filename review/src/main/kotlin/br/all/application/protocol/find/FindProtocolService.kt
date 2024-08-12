package br.all.application.protocol.find

import br.all.application.protocol.repository.ProtocolDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface FindProtocolService {
    fun findById(presenter: FindProtocolPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
    )

    @Schema(name = "FindProtocolServiceResponseModel", description = "Response model for Find Protocol Service")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val content: ProtocolDto,
    )
}