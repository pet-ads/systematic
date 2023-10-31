package br.all.domain.model.protocol

import br.all.domain.model.review.ReviewId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.utils.Language
import br.all.domain.shared.utils.phrase.Phrase

class Protocol(
    val protocolId: ProtocolId,
    val reviewId: ReviewId,

    val goal: Phrase,
    val justification: Phrase,

    val researchQuestions: Set<ResearchQuestion>,
    keywords: Set<String>,
    val searchString: String,
    informationSources: Set<SearchSource>,
    val sourcesSelectionCriteria: Phrase,

    val searchMethod: Phrase,
    studiesLanguages: Set<Language>,
    val studyTypeDefinition: Phrase,

    val selectionProcess: Phrase,
    selectionCriteria: Set<Criteria>,

    val dataCollectionProcess: Phrase,
    val analysisAndSynthesisProcess: Phrase,

    extractionFormFields: Set<QuestionId> = emptySet(),
    qualityFormFields: Set<QuestionId> = emptySet(),
    val picoc: PICOC? = null,
) : Entity(protocolId) {
    private val _keywords = keywords.toMutableSet()
    val keywords get() = _keywords.toSet()

    private val _informationSources = informationSources.toMutableSet()
    val informationSources get() = _informationSources.toSet()

    private val _studiesLanguages = studiesLanguages.toMutableSet()
    val studiesLanguages get() = _studiesLanguages.toSet()

    private val _selectionCriteria = selectionCriteria.toMutableSet()
    val selectionCriteria get() = _selectionCriteria.toSet()

    private val _extractionFormFields = extractionFormFields.toMutableSet()
    val extractionFormFields get() = _extractionFormFields.toSet()

    private val _qualityFormFields = qualityFormFields.toMutableSet()
    val qualityFormFields get() = _qualityFormFields.toSet()

    fun addKeyword(keyword: String) = _keywords.add(keyword)

    fun removeKeyword(keyword: String) = _keywords.removeKeepingOne(keyword)
                                            { "There must be at least one keyword defined!" }

    fun addSearchSource(searchSource: SearchSource) = _informationSources.add(searchSource)

    fun removeSearchSource(searchSource: SearchSource) = _informationSources.removeKeepingOne(searchSource)
                                                { "There must be at least one search source!" }

    fun addLanguage(language: Language) = _studiesLanguages.add(language)

    fun removeLanguage(language: Language) = _studiesLanguages.removeKeepingOne(language)
                                                        { "At least one language must be specified!" }

    fun addSelectionCriteria(criteria: Criteria) = _selectionCriteria.add(criteria)

    fun removeSelectionCriteria(criteria: Criteria) = _selectionCriteria.removeKeepingOne(criteria)
                                                        { "The protocol must specify the study selection criteria" }

    fun addExtractionField(questionId: QuestionId) = _extractionFormFields.add(questionId)

    fun removeExtractionField(questionId: QuestionId) = _extractionFormFields.removeKeepingOne(questionId)
                                    { "There must be specified at least one question for the extraction form!" }

    fun addQualityField(questionId: QuestionId) = _qualityFormFields.add(questionId)

    fun removeQualityField(questionId: QuestionId) = _qualityFormFields.removeKeepingOne(questionId)
                                    { "There must be specified at least one question for the quality form!" }

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    private inline fun <T> MutableSet<T>.removeKeepingOne(element: T, message: () -> String) =
        if (size > 1) remove(element) else throw IllegalStateException(message())

    fun validate(): Notification {
        val notification = Notification()

        if (searchString.isBlank())
            notification.addError("The search string cannot be blank!")
        if (researchQuestions.isEmpty())
            notification.addError("The protocol must specify at least one research question!")
        if (_keywords.isEmpty() || _keywords.any { it.isBlank() })
            notification.addError("There must be at least one keyword and none of them can be blank!")
        if (_informationSources.isEmpty())
            notification.addError("The protocol must specify the information sources for its systematic study!")
        if (_studiesLanguages.isEmpty())
            notification.addError("The protocol must specify the studies languages!")
        if (_selectionCriteria.none { it.type == Criteria.CriteriaType.INCLUSION })
            notification.addError("At least one studies inclusion criterion must be given!")
        if (_selectionCriteria.none { it.type == Criteria.CriteriaType.EXCLUSION })
            notification.addError("At least one studies exclusion criterion must be given!")

        return notification
    }
}
