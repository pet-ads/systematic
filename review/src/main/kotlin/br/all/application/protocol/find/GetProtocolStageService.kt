package br.all.application.protocol.find

import java.util.UUID

interface GetProtocolStageService {
    fun getStage(presenter: GetProtocolStagePresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val stage: ProtocolStage
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