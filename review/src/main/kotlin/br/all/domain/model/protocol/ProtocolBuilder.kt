package br.all.domain.model.protocol

import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.valueobject.Language

class ProtocolBuilder private constructor() :
    IdentificationStep,
    GoalStep,
    JustificationStep,
    ResearchQuestionsStep,
    SearchMethodDefinitionStep,
    SourcesDefinitionStep,
    SourcesCriteriaStep,
    StudiesDefinitionStep,
    StudiesSelectionStep,
    SelectionCriteriaStep,
    DataCollectionStep,
    DataAnalysisStep,
    BuildingStep {
    private var protocolId: ProtocolId? = null
    private var systematicStudyId: SystematicStudyId? = null
    private lateinit var keywords: Set<String>
    private lateinit var goal: String
    private lateinit var justification: String
    private lateinit var researchQuestions: Set<ResearchQuestion>
    private var picoc: Picoc? = null
    private lateinit var searchMethod: String
    private lateinit var searchString: String
    private lateinit var informationSources: Set<SearchSource>
    private lateinit var sourcesSelectionCriteria: String
    private lateinit var studiesLanguages: Set<Language>
    private lateinit var studyTypeDefinition: String
    private lateinit var selectionProcess: String
    private lateinit var selectionCriteria: Set<Criteria>
    private lateinit var dataCollectionProcess: String
    private lateinit var analysisAndSynthesisProcess: String
    private var extractionQuestions = emptySet<QuestionId>()
    private var robQuestions = emptySet<QuestionId>()

    companion object {
        fun with(systematicStudyId: SystematicStudyId, keywords: Set<String>) =
            ProtocolBuilder().identifiedBy(systematicStudyId, keywords)
    }

    override fun identifiedBy(systematicStudyId: SystematicStudyId, keywords: Set<String>) =
        apply {
            this.protocolId = ProtocolId(systematicStudyId.value)
            this.systematicStudyId = systematicStudyId
            this.keywords = keywords
        }

    override fun researchesFor(goal: String) = apply { this.goal = goal }

    override fun because(justification: String) = apply { this.justification = justification }

    override fun toAnswer(researchQuestions: Set<ResearchQuestion>) = apply {
        this.researchQuestions = researchQuestions
    }

    override fun followingSearchProcess(searchMethod: String, searchString: String) = apply {
        this.searchMethod = searchMethod
        this.searchString = searchString
    }

    override fun inSearchSources(informationSources: Set<SearchSource>) = apply { this.informationSources = informationSources }

    override fun selectedBecause(sourcesSelectionCriteria: String) = apply {
        this.sourcesSelectionCriteria = sourcesSelectionCriteria
    }

    override fun searchingStudiesIn(
        studiesLanguages: Set<Language>,
        studyTypeDefinition: String
    ) = apply {
        this.studiesLanguages = studiesLanguages
        this.studyTypeDefinition = studyTypeDefinition
    }

    override fun followingSelectionProcess(selectionProcess: String) = apply {
        this.selectionProcess = selectionProcess
    }

    override fun withElegibilityCriteria(selectionCriteria: Set<Criteria>) = apply {
        this.selectionCriteria = selectionCriteria
    }

    override fun followingDataCollectionProcess(dataCollectionProcess: String) = apply {
        this.dataCollectionProcess = dataCollectionProcess
    }

    override fun followingSynthesisProcess(analysisAndSynthesisProcess: String) = apply {
        this.analysisAndSynthesisProcess = analysisAndSynthesisProcess
    }

    override fun extractDataByAnswering(extractionQuestions: Set<QuestionId>) = apply {
        this.extractionQuestions = extractionQuestions
    }

    override fun qualityFormConsiders(robQuestions: Set<QuestionId>) = apply {
        this.robQuestions = robQuestions
    }

    override fun withPICOC(picoc: Picoc?) = apply { this.picoc = picoc }

    override fun build() = Protocol(
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

interface IdentificationStep {
    fun identifiedBy(systematicStudyId: SystematicStudyId, keywords: Set<String>): GoalStep
}

interface GoalStep {
    infix fun researchesFor(goal: String): JustificationStep
}

interface JustificationStep {
    infix fun because(justification: String): ResearchQuestionsStep
}

interface ResearchQuestionsStep {
    fun toAnswer(researchQuestions: Set<ResearchQuestion>): SearchMethodDefinitionStep
}

interface SearchMethodDefinitionStep {
    fun followingSearchProcess(searchMethod: String, searchString: String): SourcesDefinitionStep
}

interface SourcesDefinitionStep {
    infix fun inSearchSources(informationSources: Set<SearchSource>): SourcesCriteriaStep
}

interface SourcesCriteriaStep {
    infix fun selectedBecause(sourcesSelectionCriteria: String): StudiesDefinitionStep
}

interface StudiesDefinitionStep {
    fun searchingStudiesIn(studiesLanguages: Set<Language>, studyTypeDefinition: String): StudiesSelectionStep
}

interface StudiesSelectionStep {
    infix fun followingSelectionProcess(selectionProcess: String): SelectionCriteriaStep
}

interface SelectionCriteriaStep {
    infix fun withElegibilityCriteria(selectionCriteria: Set<Criteria>): DataCollectionStep
}

interface DataCollectionStep {
    infix fun followingDataCollectionProcess(dataCollectionProcess: String): DataAnalysisStep
}

interface DataAnalysisStep {
    infix fun followingSynthesisProcess(analysisAndSynthesisProcess: String): BuildingStep
}

interface BuildingStep {
    infix fun extractDataByAnswering(extractionQuestions: Set<QuestionId>): BuildingStep

    infix fun qualityFormConsiders(robQuestions: Set<QuestionId>): BuildingStep

    infix fun withPICOC(picoc: Picoc?): BuildingStep

    fun build(): Protocol
}
