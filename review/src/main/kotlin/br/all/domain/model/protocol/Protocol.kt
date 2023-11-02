package br.all.domain.model.protocol

import br.all.domain.model.review.ReviewId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.utils.Language
import br.all.domain.shared.utils.phrase.Phrase
import br.all.domain.shared.utils.toNeverEmptyMutableSet

class Protocol internal constructor(
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

    extractionQuestions: Set<QuestionId> = emptySet(),
    robQuestions: Set<QuestionId> = emptySet(),
    val picoc: PICOC? = null,
) : Entity(protocolId) {
    private val _keywords = keywords.toNeverEmptyMutableSet()
    val keywords get() = _keywords.toSet()

    private val _informationSources = informationSources.toNeverEmptyMutableSet()
    val informationSources get() = _informationSources.toSet()

    private val _studiesLanguages = studiesLanguages.toNeverEmptyMutableSet()
    val studiesLanguages get() = _studiesLanguages.toSet()

    private val _selectionCriteria = selectionCriteria.toNeverEmptyMutableSet()
    val selectionCriteria get() = _selectionCriteria.toSet()

    private val _extractionQuestions = extractionQuestions.toMutableSet()
    val extractionQuestions get() = _extractionQuestions.toSet()

    private val _robQuestions = robQuestions.toMutableSet()
    val ropQuestions get() = _robQuestions.toSet()

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    companion object {
        fun write() = ProtocolBuilder.start()
    }

    fun validate(): Notification {
        val notification = Notification()

        if (searchString.isBlank())
            notification.addError("The search string cannot be blank!")
        if (_selectionCriteria.none { it.type == Criteria.CriteriaType.INCLUSION })
            notification.addError("At least one studies inclusion criterion must be given!")
        if (_selectionCriteria.none { it.type == Criteria.CriteriaType.EXCLUSION })
            notification.addError("At least one studies exclusion criterion must be given!")

        return notification
    }

    fun addKeyword(keyword: String) = _keywords.add(keyword)
    fun removeKeyword(keyword: String) = _keywords.remove(keyword)
    fun addSearchSource(searchSource: SearchSource) = _informationSources.add(searchSource)
    fun removeSearchSource(searchSource: SearchSource) = _informationSources.remove(searchSource)
    fun addLanguage(language: Language) = _studiesLanguages.add(language)
    fun removeLanguage(language: Language) = _studiesLanguages.remove(language)
    fun addSelectionCriteria(criteria: Criteria) = _selectionCriteria.add(criteria)
    fun removeSelectionCriteria(criteria: Criteria) = _selectionCriteria.remove(criteria)
    fun addExtractionField(questionId: QuestionId) = _extractionQuestions.add(questionId)
    fun removeExtractionField(questionId: QuestionId) = _extractionQuestions.remove(questionId)
    fun addQualityField(questionId: QuestionId) = _robQuestions.add(questionId)
    fun removeQualityField(questionId: QuestionId) = _robQuestions.remove(questionId)
}
