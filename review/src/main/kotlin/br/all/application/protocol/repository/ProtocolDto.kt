package br.all.application.protocol.repository

import br.all.domain.model.protocol.Criteria
import br.all.domain.shared.utils.Language
import java.util.*

data class ProtocolDto(
    val id: UUID,
    val reviewId: UUID,

    val goal: String,
    val justification: String,

    val researchQuestions: Set<String>,
    val keywords: Set<String>,
    val searchString: String,
    val informationSources: Set<String>,
    val sourcesSelectionCriteria: String,

    val searchMethod: String,
    val studiesLanguages: Set<Language.LangType>,
    val studyTypeDefinition: String,

    val selectionProcess: String,
    val selectionCriteria: Set<Pair<String, Criteria.CriteriaType>>,

    val dataCollectionProcess: String,
    val analysisAndSynthesisProcess: String,

    val extractionQuestions: Set<Int>,
    val robQuestions: Set<Int>,

    val picoc: PICOCDto?,
)
