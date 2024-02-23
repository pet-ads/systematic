package br.all.infrastructure.protocol

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("protocol")
data class ProtocolDocument(
    @Id val id: UUID,

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

    val population: String?,
    val intervention: String?,
    val control: String?,
    val outcome: String?,
    val context: String?,
)
