package br.all.domain.model.protocol

import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.valueobject.Language

class ProtocolBuilder private constructor() {
    private var protocolId: ProtocolId? = null
    private var systematicStudyId: SystematicStudyId? = null
    private var keywords = emptySet<String>()
    private var goal: String? = null
    private var justification: String? = null
    private var researchQuestions = emptySet<ResearchQuestion>()
    private var picoc: Picoc? = null
    private var searchMethod: String? = null
    private var searchString: String? = null
    private var informationSources = emptySet<SearchSource>()
    private var sourcesSelectionCriteria: String? = null
    private var studiesLanguages = emptySet<Language>()
    private var studyTypeDefinition: String? = null
    private var selectionProcess: String? = null
    private var selectionCriteria = emptySet<Criterion>()
    private var dataCollectionProcess: String? = null
    private var analysisAndSynthesisProcess: String? = null
    private var extractionQuestions = emptySet<QuestionId>()
    private var robQuestions = emptySet<QuestionId>()

    companion object {
       fun with(systematicStudyId: SystematicStudyId, keywords: Set<String>) = ProtocolBuilder().identifiedBy(
           systematicStudyId,
           keywords,
       )
    }
    fun identifiedBy(systematicStudyId: SystematicStudyId, keywords: Set<String>) = apply {
        this.protocolId = ProtocolId(systematicStudyId.value())
        this.systematicStudyId = systematicStudyId
        this.keywords = keywords
    }

    infix fun researchesFor(goal: String?) = apply { this.goal = goal }

    infix fun because(justification: String?) = apply { this.justification = justification }

    infix fun toAnswer(researchQuestions: Set<ResearchQuestion>) = apply {
        this.researchQuestions = researchQuestions
    }

    fun followingSearchProcess(searchMethod: String?, searchString: String?) = apply {
        this.searchMethod = searchMethod
        this.searchString = searchString
    }

    infix fun inSearchSources(informationSources: Set<SearchSource>) = apply { this.informationSources = informationSources }

    infix fun selectedBecause(sourcesSelectionCriteria: String?) = apply {
        this.sourcesSelectionCriteria = sourcesSelectionCriteria
    }

    fun searchingStudiesIn(
        studiesLanguages: Set<Language>,
        studyTypeDefinition: String?,
    ) = apply {
        this.studiesLanguages = studiesLanguages
        this.studyTypeDefinition = studyTypeDefinition
    }

    infix fun followingSelectionProcess(selectionProcess: String?) = apply {
        this.selectionProcess = selectionProcess
    }

    infix fun withEligibilityCriteria(selectionCriteria: Set<Criterion>) = apply {
        this.selectionCriteria = selectionCriteria
    }

    infix fun followingDataCollectionProcess(dataCollectionProcess: String?) = apply {
        this.dataCollectionProcess = dataCollectionProcess
    }

    infix fun followingSynthesisProcess(analysisAndSynthesisProcess: String?) = apply {
        this.analysisAndSynthesisProcess = analysisAndSynthesisProcess
    }

    infix fun extractDataByAnswering(extractionQuestions: Set<QuestionId>) = apply {
        this.extractionQuestions = extractionQuestions
    }

    infix fun qualityFormConsiders(robQuestions: Set<QuestionId>) = apply {
        this.robQuestions = robQuestions
    }

    infix fun withPICOC(picoc: Picoc?) = apply { this.picoc = picoc }

    fun build() = Protocol(
        protocolId = protocolId as ProtocolId,
        systematicStudyId = systematicStudyId as SystematicStudyId,
        keywords = keywords,
        goal = goal,
        justification = justification,
        researchQuestions = researchQuestions,
        picoc = picoc,
        searchMethod = searchMethod,
        searchString = searchString,
        informationSources = informationSources,
        sourcesSelectionCriteria = sourcesSelectionCriteria,
        studiesLanguages = studiesLanguages,
        studyTypeDefinition = studyTypeDefinition,
        selectionProcess = selectionProcess,
        selectionCriteria = selectionCriteria,
        dataCollectionProcess = dataCollectionProcess,
        analysisAndSynthesisProcess = analysisAndSynthesisProcess,
        extractionQuestions = extractionQuestions,
        robQuestions = robQuestions
    )
}
