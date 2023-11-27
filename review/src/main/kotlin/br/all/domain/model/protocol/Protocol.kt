package br.all.domain.model.protocol

import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.utils.Language
import br.all.domain.shared.utils.Phrase
import br.all.domain.shared.utils.requireThatExists
import br.all.domain.shared.utils.toNeverEmptyMutableSet

class Protocol internal constructor(
    val protocolId: ProtocolId,
    val systematicStudyId: SystematicStudyId,

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
    val robQuestions get() = _robQuestions.toSet()

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

    fun removeKeyword(keyword: String) {
        requireThatExists(keyword in _keywords)
            { "Unable to remove a keyword that are in the protocol! Provided: $keyword" }
        _keywords.remove(keyword)
    }

    fun addInformationSource(searchSource: SearchSource) = _informationSources.add(searchSource)

    fun removeInformationSource(informationSource: SearchSource) {
        requireThatExists(informationSource in _informationSources)
            { "Unable to remove a information source that is not in the protocol! Provided: $informationSource" }
        _informationSources.remove(informationSource)
    }

    fun addLanguage(language: Language) = _studiesLanguages.add(language)

    fun removeLanguage(language: Language) {
        requireThatExists(language in _studiesLanguages)
            { "Unable to remove a language that is not in the protocol! Provided: $language" }
        _studiesLanguages.remove(language)
    }

    fun addSelectionCriteria(criteria: Criteria) = _selectionCriteria.add(criteria)

    fun removeSelectionCriteria(criteria: Criteria) {
        requireThatExists(criteria in _selectionCriteria)
            { "Unable to remove a criteria that has never been  defined in the protocol! Provided: $criteria" }
        check(isAbleToRemoveCriteriaWithSameTypeOf(criteria))
            { "Cannot remove $criteria because it would cause in no criteria of its type!" }
        _selectionCriteria.remove(criteria)
    }

    private fun isAbleToRemoveCriteriaWithSameTypeOf(criteria: Criteria): Boolean {
        return _selectionCriteria.count { it.type == criteria.type } > 1
    }

    fun addExtractionQuestion(questionId: QuestionId) = _extractionQuestions.add(questionId)

    fun removeExtractionQuestion(questionId: QuestionId) {
        requireThatExists(questionId in _extractionQuestions)
            { "Unable to remove a question that does not belongs to this protocol! Provided: $questionId" }
        _extractionQuestions.remove(questionId)
    }

    fun addRobQuestion(questionId: QuestionId) = _robQuestions.add(questionId)

    fun removeRobQuestion(questionId: QuestionId) {
        requireThatExists(questionId in _robQuestions)
            { "Unable to remove a question that does not belongs to this protocol! Provided: $questionId" }
        _robQuestions.remove(questionId)
    }
}
