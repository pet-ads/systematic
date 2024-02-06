package br.all.domain.model.protocol

import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.utils.exists
import br.all.domain.shared.valueobject.Language
import java.util.*

class Protocol internal constructor(
    protocolId: ProtocolId,
    val systematicStudyId: SystematicStudyId,

    goal: String?,
    justification: String?,

    researchQuestions: Set<ResearchQuestion>,
    keywords: Set<String>,
    searchString: String?,
    informationSources: Set<SearchSource>,
    sourcesSelectionCriteria: String?,

    searchMethod: String?,
    studiesLanguages: Set<Language>,
    studyTypeDefinition: String?,

    selectionProcess: String?,
    selectionCriteria: Set<Criterion>,

    dataCollectionProcess: String?,
    analysisAndSynthesisProcess: String?,

    extractionQuestions: Set<QuestionId> = emptySet(),
    robQuestions: Set<QuestionId> = emptySet(),
    var picoc: Picoc? = null,
) : Entity<UUID>(protocolId) {
    var goal: String? = null
        set(value) {
            if (field != null) requireNotNull(value) { "Unable to clear the goal once it is written" }
            if (value != null) require(value.isNotBlank()) { "The goal cannot be an empty string" }
            field = value
        }

    var justification: String? = null
        set(value) {
            if (field != null) requireNotNull(value) { "Unable to clear the justification once it is written" }
            if (value != null) require(value.isNotBlank()) { "The justification cannot be an empty string" }
            field = value
        }

    private val _researchQuestions = researchQuestions.toMutableSet()
    val researchQuestions get() = _researchQuestions.toSet()

    private val _keywords = keywords.toMutableSet()
    val keywords get() = _keywords.toSet()

    var searchString: String? = null
        set(value) {
            if (field != null) requireNotNull(value) { "Unable to clear the search string once it is written"}
            if (value != null) require(value.isNotBlank()) { "The search string must not be blank!" }
            field = value
        }

    private val _informationSources = informationSources.toMutableSet()
    val informationSources get() = _informationSources.toSet()

    var sourcesSelectionCriteria: String? = null
        set(value) {
            if (field != null)
                requireNotNull(value) { "Unable to clear the sources selection criteria description once it is written" }
            if (value != null)
                require(value.isNotBlank()) { "The sources selection criteria description must not be blank" }
            field = value
        }

    var searchMethod: String? = null
        set(value) {
            if (field != null) requireNotNull(value) { "Unable to clear the search method description once it is written" }
            if (value != null) require(value.isNotBlank()) { "The search method description must not be blank" }
            field = value
        }

    private val _studiesLanguages = studiesLanguages.toMutableSet()
    val studiesLanguages get() = _studiesLanguages.toSet()

    var studyTypeDefinition: String? = null
        set(value) {
            if (field != null) requireNotNull(value) { "Unable to clear the study type definition once it is written" }
            if (value != null) require(value.isNotBlank()) { "The study type definition must not be blank" }
            field = value
        }

    var selectionProcess: String? = null
        set(value) {
            if (field != null) requireNotNull(value) { "Unable to clear the selection process description once it is written" }
            if (value != null) require(value.isNotBlank()) { "The selection process description must not be blank" }
            field = value
        }

    private val _selectionCriteria = selectionCriteria.toMutableSet()
    val selectionCriteria get() = _selectionCriteria.toSet()

    var dataCollectionProcess: String? = null
        set(value) {
            if (field != null)
                requireNotNull(value) { "Unable to clear the data collection process description once it is written" }
            if (value != null)
                require(value.isNotBlank()) { "The data collection process description must not be blank" }
            field = value
        }

    var analysisAndSynthesisProcess: String? = null
        set(value) {
            if (field != null)
                requireNotNull(value) { "Unable to clear analysis and synthesis process description once it is written" }
            if (value != null)
                require(value.isNotBlank()) { "The analysis and synthesis process description must not be blank" }
            field = value
        }

    private val _extractionQuestions = extractionQuestions.toMutableSet()
    val extractionQuestions get() = _extractionQuestions.toSet()

    private val _robQuestions = robQuestions.toMutableSet()
    val robQuestions get() = _robQuestions.toSet()

    init {
        this.goal = goal
        this.justification = justification
        this.searchString = searchString
        this.sourcesSelectionCriteria = sourcesSelectionCriteria
        this.searchMethod = searchMethod
        this.studyTypeDefinition = studyTypeDefinition
        this.selectionProcess = selectionProcess
        this.dataCollectionProcess = dataCollectionProcess
        this.analysisAndSynthesisProcess = analysisAndSynthesisProcess

        require(keywords.none { it.isBlank() }) { "Protocol must not contain any blank keyword" }
    }

    fun addResearchQuestion(question: ResearchQuestion) = _researchQuestions.add(question)

    fun removeResearchQuestion(question: ResearchQuestion) {
        check(_researchQuestions.isNotEmpty()) { "Unable to remove any research question because none exist!" }
        exists(question in _researchQuestions) {
            "Unable to remove research question \"$question\", because it does not belongs to protocol"
        }
        _researchQuestions.remove(question)
    }

    fun addKeyword(keyword: String) {
        require(keyword.isNotBlank()) { "Protocol must not have blank keywords" }
        _keywords.add(keyword)
    }

    fun removeKeyword(keyword: String) {
        check(_keywords.isNotEmpty()) { "Unable to remove keyword from a protocol that does not have anyone!" }
        exists(keyword in _keywords) {
            "Unable to remove a keyword that are in the protocol! Provided: $keyword"
        }
        _keywords.remove(keyword)
    }

    fun addInformationSource(searchSource: SearchSource) = _informationSources.add(searchSource)

    fun removeInformationSource(informationSource: SearchSource) {
        check(_informationSources.isNotEmpty()) { "Unable to remove any information source because none exist!" }
        exists(informationSource in _informationSources) {
            "Unable to remove a information source that is not in the protocol! Provided: $informationSource"
        }
        _informationSources.remove(informationSource)
    }

    fun addLanguage(language: Language) = _studiesLanguages.add(language)

    fun removeLanguage(language: Language) {
        check(_studiesLanguages.isNotEmpty()) { "There is no languages to remove from this protocol" }
        exists(language in _studiesLanguages) {
            "Unable to remove a language that is not in the protocol! Provided: $language"
        }
        _studiesLanguages.remove(language)
    }

    fun addSelectionCriteria(criterion: Criterion) = _selectionCriteria.add(criterion)
    fun removeSelectionCriteria(criterion: Criterion) {
        check(_selectionCriteria.isNotEmpty()) { "There is not any criterion to remove from this protocol" }
        exists(criterion in _selectionCriteria) {
            "Unable to remove a criteria that has never been  defined in the protocol! Provided: $criterion"
        }
        _selectionCriteria.remove(criterion)
    }

    fun addExtractionQuestion(questionId: QuestionId) = _extractionQuestions.add(questionId)

    fun removeExtractionQuestion(questionId: QuestionId) {
        check(_extractionQuestions.isNotEmpty()) { "Unable to remove any extraction question because none exist" }
        exists(questionId in _extractionQuestions) {
            "Unable to remove a question that does not belongs to this protocol! Provided: $questionId"
        }
        _extractionQuestions.remove(questionId)
    }

    fun addRobQuestion(questionId: QuestionId) = _robQuestions.add(questionId)

    fun removeRobQuestion(questionId: QuestionId) {
        check(_robQuestions.isNotEmpty()) { "Unable to remove any rob question because none exist" }
        exists(questionId in _robQuestions) {
            "Unable to remove a question that does not belongs to this protocol! Provided: $questionId"
        }
        _robQuestions.remove(questionId)
    }

    companion object {
        fun with(systematicStudyId: SystematicStudyId, keywords: Set<String>) = ProtocolBuilder.with(
            systematicStudyId,
            keywords,
        )
    }
}
