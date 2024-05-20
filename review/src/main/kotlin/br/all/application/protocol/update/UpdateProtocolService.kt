package br.all.application.protocol.update

import br.all.application.protocol.repository.CriterionDto
import br.all.application.protocol.repository.PicocDto
import java.util.*

interface UpdateProtocolService {
    fun update(presenter: UpdateProtocolPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,

        val goal: String? = null,
        val justification: String? = null,
        val researchQuestions: Set<String> = emptySet(),
        val keywords: Set<String> = emptySet(),

        val searchString: String? = null,
        val informationSources: Set<String> = emptySet(),
        val sourcesSelectionCriteria: String? = null,
        val searchMethod: String? = null,

        val studiesLanguages: Set<String> = emptySet(),
        val studyTypeDefinition: String? = null,

        val selectionProcess: String? = null,
        val eligibilityCriteria: Set<CriterionDto> = emptySet(),

        val dataCollectionProcess: String? = null,
        val analysisAndSynthesisProcess: String? = null,

        val picoc: PicocDto? = null,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
    )
}