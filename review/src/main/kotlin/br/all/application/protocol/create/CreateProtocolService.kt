package br.all.application.protocol.create

import java.util.*

interface CreateProtocolService {
    fun create(presenter: CreateProtocolPresenter, request: RequestModel)

    data class RequestModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,

        val goal: String? = null,
        val justification: String? = null,
        val keywords: Set<String> = emptySet(),

        val searchString: String? = null,
        val informationSources: Set<String> = emptySet(),
        val sourcesSelectionCriteria: String? = null,
        val searchMethod: String? = null,

        val studiesLanguages: Set<String> = emptySet(),
        val studyTypeDefinition: String? = null,

        val selectionProcess: String? = null,
        val dataCollectionProcess: String? = null,
        val analysisAndSynthesisProcess: String? = null,
    )

    data class ResponseModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
    )
}
