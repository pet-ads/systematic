package br.all.protocol.requests

import br.all.application.protocol.repository.CriterionDto
import br.all.application.protocol.repository.PicocDto
import br.all.application.protocol.update.UpdateProtocolService
import java.util.*

data class PutRequest(
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

    val picoc: PicocRequest? = null,
) {
    fun toUpdateRequestModel(user: UUID, systematicStudy: UUID) = UpdateProtocolService.RequestModel(
        user,
        systematicStudy,
        goal,
        justification,
        researchQuestions,
        keywords,
        searchString,
        informationSources,
        sourcesSelectionCriteria,
        searchMethod,
        studiesLanguages,
        studyTypeDefinition,
        selectionProcess,
        eligibilityCriteria,
        dataCollectionProcess,
        analysisAndSynthesisProcess,
        picoc?.let { PicocDto(it.population, it.intervention, it.control, it.outcome, it.context) },
    )

    data class PicocRequest(
        val population: String? = null,
        val intervention: String? = null,
        val control: String? = null,
        val outcome: String? = null,
        val context: String? = null,
    )
}