package br.all.application.protocol.find

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface FindProtocolStageService {
    fun getStage(presenter: FindProtocolStagePresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID
    )

    @Schema(name = "FindProtocolStageServiceResponseModel", description = "Response model for Find Protocol Stage Service")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val currentStage: ProtocolStage
    )

    enum class ProtocolStage {
        GENERAL_DEFINITION,
        RESEARCH_QUESTIONS,
        PICOC,
        ELIGIBILITY_CRITERIA,
        INFORMATION_SOURCES_AND_SEARCH_STRATEGY,
        SELECTION_AND_EXTRACTION,
        RISK_OF_BIAS,
        ANALYSIS_AND_SYNTHESIS_METHOD,

        IDENTIFICATION,
        SELECTION,
        EXTRACTION,
        GRAPHICS
    }
}