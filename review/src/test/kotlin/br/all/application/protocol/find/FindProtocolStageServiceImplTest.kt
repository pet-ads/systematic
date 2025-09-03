package br.all.application.protocol.find

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.util.TestDataFactory as ProtocolFactory
import br.all.application.study.util.TestDataFactory as StudyReviewFactory
import br.all.application.question.util.TestDataFactory as QuestionFactory
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import kotlin.test.Test
import br.all.application.protocol.find.FindProtocolStageService.RequestModel
import br.all.application.protocol.find.FindProtocolStageService.ResponseModel
import br.all.application.protocol.find.FindProtocolStageService.ProtocolStage // Assuming this enum is updated
import br.all.application.protocol.repository.CriterionDto
import br.all.application.protocol.repository.PicocDto
import br.all.application.protocol.repository.ProtocolDto
import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.study.repository.StudyReviewDto
import br.all.domain.model.question.QuestionContextEnum
import io.mockk.verify

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindProtocolStageServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK(relaxUnitFun = true)
    private lateinit var questionRepository: QuestionRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: FindProtocolStagePresenter

    @InjectMockKs
    private lateinit var sut: FindProtocolStageServiceImpl

    private lateinit var precondition: PreconditionCheckerMockingNew
    private lateinit var protocolFactory: ProtocolFactory
    private lateinit var studyReviewFactory: StudyReviewFactory
    private lateinit var questionFactory: QuestionFactory

    private lateinit var researcherId: UUID
    private lateinit var systematicStudyId: UUID

    @BeforeEach
    fun setup() {
        protocolFactory = ProtocolFactory()
        studyReviewFactory = StudyReviewFactory()
        questionFactory = QuestionFactory()

        researcherId = protocolFactory.researcher
        systematicStudyId = protocolFactory.systematicStudy

        precondition = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            researcherId,
            systematicStudyId
        )

        precondition.makeEverythingWork()

        every { systematicStudyRepository.findById(systematicStudyId) } returns createFullSystematicStudyDto()

        every { studyReviewRepository.findAllFromReview(any()) } returns emptyList()
        every { questionRepository.findAllBySystematicStudyId(any(), any()) } returns emptyList()
    }

    @Nested
    @DisplayName("When getting protocol stage")
    inner class SuccessfullyGettingProtocolStage {

        @Test
        fun `should return GENERAL_DEFINITION stage (T1) when systematic study name is blank`() {
            val incompleteStudyDto = createFullSystematicStudyDto(title = "")
            val protocolDto = protocolFactory.protocolDto(systematicStudy = systematicStudyId)

            every { systematicStudyRepository.findById(systematicStudyId) } returns incompleteStudyDto
            every { protocolRepository.findById(systematicStudyId) } returns protocolDto

            sut.getStage(presenter, RequestModel(researcherId, systematicStudyId))

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.GENERAL_DEFINITION)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return RESEARCH_QUESTIONS stage (T2) when T1 is complete but no research questions exist`() {
            val protocolDto = createFullProtocolDto(researchQuestions = emptySet())

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto

            sut.getStage(presenter, RequestModel(researcherId, systematicStudyId))

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.RESEARCH_QUESTIONS)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return PICOC stage (T3) when it is started but not complete`() {
            val protocolDto = createFullProtocolDto(
                picoc = PicocDto(population = "P", intervention = "I", control = null, outcome = null, context = null)
            )

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto

            sut.getStage(presenter, RequestModel(researcherId, systematicStudyId))

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.PICOC)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return ELIGIBILITY_CRITERIA stage (T4) when T3 (PICOC) is skipped and T4 is incomplete`() {
            val protocolDto = createFullProtocolDto(
                picoc = PicocDto(null, null, null, null, null),
                eligibilityCriteria = emptySet()
            )

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto

            sut.getStage(presenter, RequestModel(researcherId, systematicStudyId))

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.ELIGIBILITY_CRITERIA)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return INFORMATION_SOURCES_AND_SEARCH_STRATEGY stage (T5) when T4 is complete but T5 is not`() {
            val protocolDto = createFullProtocolDto(searchString = "")

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto

            sut.getStage(presenter, RequestModel(researcherId, systematicStudyId))

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.INFORMATION_SOURCES_AND_SEARCH_STRATEGY)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return SELECTION_AND_EXTRACTION stage (T6) when T5 is complete but T6 is not (no extraction questions)`() {
            val protocolDto = createFullProtocolDto()
            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns emptyList()

            sut.getStage(presenter, RequestModel(researcherId, systematicStudyId))

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.SELECTION_AND_EXTRACTION)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return ANALYSIS_AND_SYNTHESIS_METHOD stage (T8) when T6 is complete but T8 is not`() {
            val protocolDto = createFullProtocolDto(analysisAndSynthesisProcess = "")
            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns listOf(questionFactory.generateTextualDto())
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.ROB) } returns emptyList()

            sut.getStage(presenter, RequestModel(researcherId, systematicStudyId))

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.ANALYSIS_AND_SYNTHESIS_METHOD)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return IDENTIFICATION stage when protocol is complete and no studies are imported`() {
            val protocolDto = createFullProtocolDto()
            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns listOf(questionFactory.generateTextualDto())
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.ROB) } returns listOf(questionFactory.generateTextualDto())
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()

            sut.getStage(presenter, RequestModel(researcherId, systematicStudyId))

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.IDENTIFICATION)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return SELECTION stage when studies exist but none are included`() {
            val protocolDto = createFullProtocolDto()
            val studies = listOf(createFullStudyReviewDto(selectionStatus = "EXCLUDED"))
            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns listOf(questionFactory.generateTextualDto())
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.ROB) } returns listOf(questionFactory.generateTextualDto())
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns studies

            sut.getStage(presenter, RequestModel(researcherId, systematicStudyId))

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.SELECTION)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return EXTRACTION stage when studies are included but not extracted`() {
            val protocolDto = createFullProtocolDto()
            val studies = listOf(createFullStudyReviewDto(selectionStatus = "INCLUDED", extractionStatus = "PENDING"))
            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns listOf(questionFactory.generateTextualDto())
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.ROB) } returns listOf(questionFactory.generateTextualDto())
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns studies

            sut.getStage(presenter, RequestModel(researcherId, systematicStudyId))

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.EXTRACTION)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return GRAPHICS stage when at least one study has been fully extracted`() {
            val protocolDto = createFullProtocolDto()
            val studies = listOf(createFullStudyReviewDto(selectionStatus = "INCLUDED", extractionStatus = "INCLUDED"))
            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns listOf(questionFactory.generateTextualDto())
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.ROB) } returns listOf(questionFactory.generateTextualDto())
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns studies

            sut.getStage(presenter, RequestModel(researcherId, systematicStudyId))

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.GRAPHICS)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }
    }

    private fun createFullSystematicStudyDto(
        id: UUID = systematicStudyId,
        title: String = "A complete systematic study",
        description: String = "A complete description",
        objectives: String = "A complete objective",
    ) = SystematicStudyDto(
        id = id,
        title = title,
        description = description,
        owner = UUID.randomUUID(),
        collaborators = emptySet(),
        objectives = objectives,
    )

    private fun createFullProtocolDto(
        goal: String? = "A complete goal",
        researchQuestions: Set<String> = setOf("RQ1?"),
        picoc: PicocDto? = PicocDto("P", "I", "C", "O", "Context"),
        eligibilityCriteria: Set<CriterionDto> = setOf(
            CriterionDto("Inclusion", "INCLUSION"),
            CriterionDto("Exclusion", "EXCLUSION")
        ),
        studyTypeDefinition: String? = "Randomized Controlled Trial",
        studiesLanguages: Set<String> = setOf("English"),
        sourcesSelectionCriteria: String? = "Selection criteria",
        informationSources: Set<String> = setOf("Scopus"),
        searchMethod: String? = "A valid search method",
        keywords: Set<String> = setOf("keyword1"),
        searchString: String? = "((keyword1) AND (keyword2))",
        selectionProcess: String? = "A valid selection process",
        dataCollectionProcess: String? = "A valid data collection process",
        analysisAndSynthesisProcess: String? = "A final analysis process"
    ): ProtocolDto {
        val baseDto = protocolFactory.protocolDto(
            systematicStudy = systematicStudyId,
            goal = goal,
            justification = "A justification",
            researchQuestions = researchQuestions,
            picoc = picoc,
            eligibilityCriteria = eligibilityCriteria,
            studiesLanguages = studiesLanguages,
            sourcesSelectionCriteria = sourcesSelectionCriteria,
            informationSources = informationSources,
            searchMethod = searchMethod,
            keywords = keywords,
            selectionProcess = selectionProcess,
            analysisAndSynthesisProcess = analysisAndSynthesisProcess
        )
        return baseDto.copy(
            studyTypeDefinition = studyTypeDefinition,
            searchString = searchString,
            dataCollectionProcess = dataCollectionProcess
        )
    }

    private fun createFullStudyReviewDto(
        selectionStatus: String,
        extractionStatus: String = "PENDING"
    ) = studyReviewFactory.generateDto(
        systematicStudyId = systematicStudyId,
        selectionStatus = selectionStatus,
        extractionStatus = extractionStatus,
    )
}