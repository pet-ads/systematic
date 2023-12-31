package br.all.application.protocol.create

import br.all.application.protocol.repository.PicocDto

data class ProtocolRequestModel(
    val goal: String,
    val justification: String,

    val researchQuestions: Set<String>,
    val keywords: Set<String>,
    val searchString: String,
    val informationSources: Set<String>,
    val sourcesSelectionCriteria: String,

    val searchMethod: String,
    val studiesLanguages: Set<String>,
    val studyTypeDefinition: String,

    val selectionProcess: String,
    val selectionCriteria: Set<Pair<String, String>>,

    val dataCollectionProcess: String,
    val analysisAndSynthesisProcess: String,

    val picoc: PicocDto? = null,
)
