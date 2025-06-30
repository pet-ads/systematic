package br.all.application.protocol.find

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface GetProtocolStageService {
    fun getStage(presenter: GetProtocolStagePresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID
    )

    @Schema(name = "GetProtocolStageServiceResponseModel", description = "Response model for Get Protocol Stage Service")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val currentStage: ProtocolStage
    )

    enum class ProtocolStage {
        PROTOCOL_PART_I,
        PICOC,
        PROTOCOL_PART_II,
        PROTOCOL_PART_III,
        IDENTIFICATION,
        SELECTION,
        EXTRACTION,
        GRAPHICS,
        FINALIZATION
    }
}