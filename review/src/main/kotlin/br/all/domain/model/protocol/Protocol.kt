package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.utils.Language

class Protocol(
    protocolId: ProtocolId,
    val goal: String,
    val justification: String,
    val researchQuestions: Set<ResearchQuestion>,
    keywords: Set<String>,
    sources: Set<SearchSource>,
    val sourcesSelectionMethod: String,
    val searchMethod: String,
    studiesLanguages: Set<Language>,
    val studyTypeDefinition: String,
    val selectionMethod: String,
    selectionCriteria: Set<Criteria>,
    val extractionMethod: String,
    val qualityEvaluationMethod: String,
    val summarizationMethod: String,
    val publishingMethod: String,
    extractionFormFields: Set<QuestionId> = emptySet(),
    qualityFormFields: Set<QuestionId> = emptySet(),
    val picoc: PICOC? = null,
) : Entity(protocolId) {
    private val _keywords = keywords.toMutableSet()
    val keywords get() = _keywords.toSet()

    private val _sources = sources.toMutableSet()
    val sources get() = _sources.toSet()

    private val _studiesLanguages = studiesLanguages.toMutableSet()
    val studiesLanguages get() = _studiesLanguages.toSet()

    private val _selectionCriteria = selectionCriteria.toMutableSet()
    val selectionCriteria get() = _selectionCriteria.toSet()

    private val _extractionFormFields = extractionFormFields.toMutableSet()
    val extractionFormFields get() = _extractionFormFields.toSet()

    private val _qualityFormFields = qualityFormFields.toMutableSet()
    val qualityFormFields get() = _qualityFormFields.toSet()
}