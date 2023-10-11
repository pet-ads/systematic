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

    fun addKeyword(keyword: String) = _keywords.add(keyword)

    fun removeKeyword(keyword: String) = _keywords.removeIfSizeGreaterThanOne(keyword)
                                            { "There must be at least one keyword defined!" }

    fun addSearchSource(searchSource: SearchSource) = _sources.add(searchSource)

    fun removeSearchSource(searchSource: SearchSource) = _sources.removeIfSizeGreaterThanOne(searchSource)
                                                { "There must be at least one search source!" }

    fun addLanguage(language: Language) = _studiesLanguages.add(language)

    fun removeLanguage(language: Language) = _studiesLanguages.removeIfSizeGreaterThanOne(language)
                                                        { "At least one language must be specified!" }

    fun addSelectionCriteria(criteria: Criteria) = _selectionCriteria.add(criteria)

    fun removeSelectionCriteria(criteria: Criteria) = _selectionCriteria.removeIfSizeGreaterThanOne(criteria)
                                                        { "The protocol must specify the study selection criteria" }

    fun addExtractionField(questionId: QuestionId) = _extractionFormFields.add(questionId)

    fun removeExtractionField(questionId: QuestionId) = _extractionFormFields.removeIfSizeGreaterThanOne(questionId)
                                    { "There must be specified at least one question for the extraction form!" }

    fun addQualityField(questionId: QuestionId) = _qualityFormFields.add(questionId)

    fun removeQualityField(questionId: QuestionId) = _qualityFormFields.removeIfSizeGreaterThanOne(questionId)
                                    { "There must be specified at least one question for the quality form!" }

    private inline fun <T> MutableSet<T>.removeIfSizeGreaterThanOne(element: T, message: () -> String) =
        if (size > 1) remove(element) else throw IllegalStateException(message())
}
