package br.all.application.protocol.repository

import java.util.*

data class ProtocolDto(
    val id: UUID,
    val systematicStudy: UUID,

    val goal: String?,
    val justification: String?,

    val researchQuestions: Set<String>,
    val keywords: Set<String>,
    val searchString: String?,
    val informationSources: Set<String>,
    val sourcesSelectionCriteria: String?,

    val searchMethod: String?,
    val studiesLanguages: Set<String>,
    val studyTypeDefinition: String?,

    val selectionProcess: String?,
    val selectionCriteria: Set<Pair<String, String>>,

    val dataCollectionProcess: String?,
    val analysisAndSynthesisProcess: String?,

    val extractionQuestions: Set<UUID>,
    val robQuestions: Set<UUID>,

    val picoc: PicocDto?,
)
