package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Entity
import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudyId
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
    eligibilityCriteria: Set<Criterion>,

    dataCollectionProcess: String?,
    analysisAndSynthesisProcess: String?,

    extractionQuestions: Set<QuestionId> = emptySet(),
    robQuestions: Set<QuestionId> = emptySet(),
    var picoc: Picoc? = null,
) : Entity<UUID>(protocolId) {
    var goal: String? = null
        set(value) {
            if (value != null) require(value.isNotBlank()) { "The goal cannot be an empty string" }
            field = value
        }

    var justification: String? = null
        set(value) {
            if (value != null) require(value.isNotBlank()) { "The justification cannot be an empty string" }
            field = value
        }

    private val _researchQuestions = researchQuestions.toMutableSet()
    val researchQuestions get() = _researchQuestions.toSet()

    private val _keywords = keywords.toMutableSet()
    val keywords get() = _keywords.toSet()

    var searchString: String? = null
        set(value) {
            if (value != null) require(value.isNotBlank()) { "The search string must not be blank!" }
            field = value
        }

    private val _informationSources = informationSources.toMutableSet()
    val informationSources get() = _informationSources.toSet()

    var sourcesSelectionCriteria: String? = null
        set(value) {
            if (value != null) require(value.isNotBlank()) { "The sources selection criteria description must not be blank" }
            field = value
        }

    var searchMethod: String? = null
        set(value) {
            if (value != null) require(value.isNotBlank()) { "The search method description must not be blank" }
            field = value
        }

    private val _studiesLanguages = studiesLanguages.toMutableSet()
    val studiesLanguages get() = _studiesLanguages.toSet()

    var studyTypeDefinition: String? = null
        set(value) {
            if (value != null) require(value.isNotBlank()) { "The study type definition must not be blank" }
            field = value
        }

    var selectionProcess: String? = null
        set(value) {
            if (value != null) require(value.isNotBlank()) { "The selection process description must not be blank" }
            field = value
        }

    private val _eligibilityCriteria = eligibilityCriteria.toMutableSet()
    val eligibilityCriteria get() = _eligibilityCriteria.toSet()

    var dataCollectionProcess: String? = null
        set(value) {
            if (value != null) require(value.isNotBlank()) { "The data collection process description must not be blank" }
            field = value
        }

    var analysisAndSynthesisProcess: String? = null
        set(value) {
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

    fun replaceResearchQuestions(newQuestions: Set<ResearchQuestion>) {
        _researchQuestions.addAll(newQuestions)
        _researchQuestions.removeIf { it !in newQuestions }
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

    fun replaceKeywords(keywords: Set<String>) {
        _keywords.addAll(keywords)
        _keywords.removeIf { it !in keywords }
    }

    fun clearKeyWords(){
        _keywords.clear()
    }

    fun addInformationSource(searchSource: SearchSource) = _informationSources.add(searchSource)

    fun hasInformationSource(searchSource: SearchSource) = _informationSources.contains(searchSource)

    fun removeInformationSource(informationSource: SearchSource) {
        check(_informationSources.isNotEmpty()) { "Unable to remove any information source because none exist!" }
        exists(informationSource in _informationSources) {
            "Unable to remove a information source that is not in the protocol! Provided: $informationSource"
        }
        _informationSources.remove(informationSource)
    }

    fun replaceInformationSources(informationSources: Set<SearchSource>) {
        _informationSources.addAll(informationSources)
        _informationSources.removeIf { it !in informationSources }
    }

    fun addLanguage(language: Language) = _studiesLanguages.add(language)

    fun removeLanguage(language: Language) {
        check(_studiesLanguages.isNotEmpty()) { "There is no languages to remove from this protocol" }
        exists(language in _studiesLanguages) {
            "Unable to remove a language that is not in the protocol! Provided: $language"
        }
        _studiesLanguages.remove(language)
    }

    fun replaceLanguages(languages: Set<Language>) {
        _studiesLanguages.addAll(languages)
        _studiesLanguages.removeIf { it !in languages }
    }

    fun addEligibilityCriterion(criterion: Criterion) = _eligibilityCriteria.add(criterion)

    fun removeEligibilityCriterion(criterion: Criterion) {
        check(_eligibilityCriteria.isNotEmpty()) { "There is not any criterion to remove from this protocol" }
        exists(criterion in _eligibilityCriteria) {
            "Unable to remove a criteria that has never been  defined in the protocol! Provided: $criterion"
        }
        _eligibilityCriteria.remove(criterion)
    }

    fun replaceEligibilityCriteria(criteria: Set<Criterion>) {
        _eligibilityCriteria.addAll(criteria)
        _eligibilityCriteria.removeIf { it !in criteria }
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
        fun write(systematicStudyId: SystematicStudyId, keywords: Set<String>) =
            ProtocolBuilder.with(systematicStudyId, keywords)
    }
}
