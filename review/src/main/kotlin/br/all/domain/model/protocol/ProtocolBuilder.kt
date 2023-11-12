package br.all.domain.model.protocol

import br.all.domain.model.review.ReviewId
import br.all.domain.shared.utils.Language
import br.all.domain.shared.utils.Phrase

class ProtocolBuilder private constructor(): IdentificationStep, GoalStep, JustificationStep, ResearchQuestionsStep,
        SearchMethodDefinitionStep, SourcesDefinitionStep, SourcesCriteriaStep, StudiesDefinitionStep,
        StudiesSelectionStep, SelectionCriteriaStep, DataCollectionStep, DataAnalysisStep, BuildingStep {
    private var protocolId: ProtocolId? = null
    private var reviewId: ReviewId? = null
    private lateinit var keywords: Set<String>
    private lateinit var goal: Phrase
    private lateinit var justification: Phrase
    private lateinit var researchQuestions: Set<ResearchQuestion>
    private var picoc: PICOC? = null
    private lateinit var searchMethod: Phrase
    private lateinit var searchString: String
    private lateinit var informationSources: Set<SearchSource>
    private lateinit var sourcesSelectionCriteria: Phrase
    private lateinit var studiesLanguages: Set<Language>
    private lateinit var studyTypeDefinition: Phrase
    private lateinit var selectionProcess: Phrase
    private lateinit var selectionCriteria: Set<Criteria>
    private lateinit var dataCollectionProcess: Phrase
    private lateinit var analysisAndSynthesisProcess: Phrase
    private var extractionQuestions = emptySet<QuestionId>()
    private var robQuestions = emptySet<QuestionId>()

    companion object {
        fun start(): IdentificationStep = ProtocolBuilder()
    }

    override fun identifiedBy(protocolId: ProtocolId, reviewId: ReviewId, keywords: Set<String>) = apply {
        this.protocolId = protocolId
        this.reviewId = reviewId
        this.keywords = keywords
    }

    override fun researchesFor(goal: Phrase) = apply { this.goal = goal }

    override fun because(justification: Phrase) = apply { this.justification = justification }

    override fun toAnswer(researchQuestions: Set<ResearchQuestion>, picoc: PICOC?) = apply {
        this.researchQuestions = researchQuestions
        this.picoc = picoc
    }

    override fun searchProcessWillFollow(searchMethod: Phrase, searchString: String) = apply {
        this.searchMethod = searchMethod
        this.searchString = searchString
    }

    override fun at(informationSources: Set<SearchSource>) = apply { this.informationSources = informationSources }

    override fun selectedBecause(sourcesSelectionCriteria: Phrase) = apply {
        this.sourcesSelectionCriteria =sourcesSelectionCriteria
    }

    override fun searchStudiesOf(
        studiesLanguages: Set<Language>,
        studyTypeDefinition: Phrase
    ) = apply {
        this.studiesLanguages = studiesLanguages
        this.studyTypeDefinition = studyTypeDefinition
    }

    override fun selectionProcessWillFollowAs(selectionProcess: Phrase) = apply {
        this.selectionProcess = selectionProcess
    }

    override fun selectStudiesBy(selectionCriteria: Set<Criteria>) = apply {
        this.selectionCriteria = selectionCriteria
    }

    override fun collectDataBy(dataCollectionProcess: Phrase) = apply {
        this.dataCollectionProcess = dataCollectionProcess
    }

    override fun analyseDataBy(analysisAndSynthesisProcess: Phrase) = apply {
        this.analysisAndSynthesisProcess = analysisAndSynthesisProcess
    }

    override fun extractDataByAnswering(extractionQuestions: Set<QuestionId>) = apply {
        this.extractionQuestions = extractionQuestions
    }

    override fun qualityFormConsiders(robQuestions: Set<QuestionId>) = apply {
        this.robQuestions = robQuestions
    }

    override fun finish() = Protocol(
        protocolId = protocolId as ProtocolId,
        reviewId = reviewId as ReviewId,
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
    fun identifiedBy(protocolId: ProtocolId, reviewId: ReviewId, keywords: Set<String>): GoalStep
}

interface GoalStep {
    infix fun researchesFor(goal: Phrase): JustificationStep
}

interface JustificationStep {
    infix fun because(justification: Phrase): ResearchQuestionsStep
}

interface ResearchQuestionsStep {
    fun toAnswer(researchQuestions: Set<ResearchQuestion>, picoc: PICOC? = null): SearchMethodDefinitionStep
}

interface SearchMethodDefinitionStep {
    fun searchProcessWillFollow(searchMethod: Phrase, searchString: String): SourcesDefinitionStep
}

interface SourcesDefinitionStep {
    infix fun at(informationSources: Set<SearchSource>): SourcesCriteriaStep
}

interface SourcesCriteriaStep {
    infix fun selectedBecause(sourcesSelectionCriteria: Phrase): StudiesDefinitionStep
}

interface StudiesDefinitionStep {
    fun searchStudiesOf(studiesLanguages: Set<Language>, studyTypeDefinition: Phrase): StudiesSelectionStep
}

interface StudiesSelectionStep {
    infix fun selectionProcessWillFollowAs(selectionProcess: Phrase): SelectionCriteriaStep
}

interface SelectionCriteriaStep {
    infix fun selectStudiesBy(selectionCriteria: Set<Criteria>): DataCollectionStep
}

interface DataCollectionStep {
    infix fun collectDataBy(dataCollectionProcess: Phrase): DataAnalysisStep
}

interface DataAnalysisStep {
    infix fun analyseDataBy(analysisAndSynthesisProcess: Phrase): BuildingStep
}

interface BuildingStep {
    infix fun extractDataByAnswering(extractionQuestions: Set<QuestionId>): BuildingStep

    infix fun qualityFormConsiders(robQuestions: Set<QuestionId>): BuildingStep

    fun finish(): Protocol
}
