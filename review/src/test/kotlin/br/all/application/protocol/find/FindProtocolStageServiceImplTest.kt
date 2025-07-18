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
import br.all.application.protocol.find.FindProtocolStageService.ProtocolStage
import br.all.application.protocol.repository.CriterionDto
import br.all.application.protocol.repository.PicocDto
import br.all.application.question.repository.QuestionRepository
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
    }

    @Nested
    @DisplayName("When successfully getting protocol's current stage")
    inner class SuccessfullyGettingProtocolStage {
        @Test
        fun `should return PROTOCOL_PART_I stage when goal and justification are empty`() {
            val protocolDto = protocolFactory.protocolDto(
                systematicStudy = systematicStudyId,
                goal = null,
                justification = null
            )

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.ROB) } returns emptyList()
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns emptyList()

            val request = RequestModel(researcherId, systematicStudyId)

            sut.getStage(presenter, request)

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.PROTOCOL_PART_I)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return PICOC stage when it is not filled`() {
            val protocolDto = protocolFactory.protocolDto(
                systematicStudy = systematicStudyId,
                goal = "A goal",
                justification = "A justification",
                picoc = PicocDto(
                    population = "P",
                    intervention = null,
                    control = null,
                    outcome = null,
                    context = null
                )
            )

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.ROB) } returns emptyList()
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns emptyList()

            val request = RequestModel(researcherId, systematicStudyId)

            sut.getStage(presenter, request)

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.PICOC)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return PROTOCOL_PART_II stage when its fields are empty`() {
            val protocolDto = protocolFactory.protocolDto(
                systematicStudy = systematicStudyId,
                goal = "A goal",
                justification = "A justification",
                picoc = PicocDto(population = "P", intervention = "I", control = "C", outcome = "O", context = "C"),
                studiesLanguages = emptySet(),
                eligibilityCriteria = emptySet(),
                informationSources = emptySet(),
                keywords = emptySet()
            )

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.ROB) } returns emptyList()
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns emptyList()

            val request = RequestModel(researcherId, systematicStudyId)

            sut.getStage(presenter, request)

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.PROTOCOL_PART_II)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return PROTOCOL_PART_III stage when its conditions are not met`() {
            val protocolDto = protocolFactory.protocolDto(
                systematicStudy = systematicStudyId,
                goal = "A goal",
                justification = "A justification",
                picoc = PicocDto(population = "P", intervention = "I", control = "C", outcome = "O", context = "C"),
                keywords = setOf("test"),
                extractionQuestions = emptySet()
            )

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.ROB) } returns emptyList()
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns emptyList()

            val request = RequestModel(researcherId, systematicStudyId)

            sut.getStage(presenter, request)

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.PROTOCOL_PART_III)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return IDENTIFICATION stage when no studies have been submitted`() {
            val protocolDto = createFullProtocolDto()
            val questions = listOf(
                questionFactory.generateTextualDto()
            )

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.ROB) } returns questions
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns questions

            val request = RequestModel(researcherId, systematicStudyId)

            sut.getStage(presenter, request)

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.IDENTIFICATION)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return SELECTION stage when studies exist but none are included`() {
            val protocolDto = createFullProtocolDto()
            val studies = listOf(
                createFullStudyReviewDto(selectionStatus = "", extractionStatus = "")
            )
            val questions = listOf(
                questionFactory.generateTextualDto()
            )

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns studies
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.ROB) } returns questions
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns questions

            val request = RequestModel(researcherId, systematicStudyId)

            sut.getStage(presenter, request)

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.SELECTION)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return EXTRACTION stage when studies are included but none are extracted`() {
            val protocolDto = createFullProtocolDto()
            val studies = listOf(
                createFullStudyReviewDto(selectionStatus = "INCLUDED", extractionStatus = "")
            )
            val questions = listOf(
                questionFactory.generateTextualDto()
            )

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns studies
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.ROB) } returns questions
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns questions

            val request = RequestModel(researcherId, systematicStudyId)

            sut.getStage(presenter, request)

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.EXTRACTION)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }

        @Test
        fun `should return GRAPHICS stage when studies have been extracted`() {
            val protocolDto = createFullProtocolDto()
            val studies = listOf(
                createFullStudyReviewDto(selectionStatus = "INCLUDED", extractionStatus = "INCLUDED")
            )
            val questions = listOf(
                questionFactory.generateTextualDto()
            )

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns studies
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.ROB) } returns questions
            every { questionRepository.findAllBySystematicStudyId(systematicStudyId, QuestionContextEnum.EXTRACTION) } returns questions

            val request = RequestModel(researcherId, systematicStudyId)

            sut.getStage(presenter, request)

            val expectedResponse = ResponseModel(researcherId, systematicStudyId, ProtocolStage.GRAPHICS)
            verify(exactly = 1) { presenter.prepareSuccessView(expectedResponse) }
        }
    }


    private fun createFullProtocolDto() = protocolFactory.protocolDto(
        systematicStudy = systematicStudyId,
        goal = "A goal",
        justification = "A justification",
        picoc = PicocDto(population = "P", intervention = "I", control = "C", outcome = "O", context = "C"),
        studiesLanguages = setOf("English"),
        eligibilityCriteria = setOf(
            CriterionDto("test description", "INCLUSION"),
            CriterionDto("test description", "EXCLUSION")
        ),
        informationSources = setOf("IEEE"),
        keywords = setOf("test"),
        sourcesSelectionCriteria = "criteria",
        searchMethod = "method",
        selectionProcess = "process",
        extractionQuestions = setOf(UUID.randomUUID()),
        robQuestions = setOf(UUID.randomUUID()),
        researchQuestions = setOf("RQ1?"),
        analysisAndSynthesisProcess = "process"
    )

    private fun createFullStudyReviewDto(selectionStatus: String, extractionStatus: String) = studyReviewFactory.generateDto(
        studyReviewId = 1L,
        systematicStudyId = systematicStudyId,
        searchSessionId = UUID.randomUUID(),
        type = "type",
        title = "title",
        year = 2025,
        authors = "authors",
        venue = "venue",
        abstract = "abstract",
        keywords = emptySet(),
        references = emptyList(),
        doi = "doi",
        sources = emptySet(),
        criteria = emptySet(),
        formAnswers = emptyMap(),
        robAnswers = emptyMap(),
        comments = "comments",
        readingPriority = "HIGH",
        extractionStatus = extractionStatus,
        selectionStatus = selectionStatus,
        score = 10
    )
}
