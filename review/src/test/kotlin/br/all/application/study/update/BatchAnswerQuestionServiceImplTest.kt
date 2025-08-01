package br.all.application.study.update

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.BatchAnswerQuestionServiceImpl
import br.all.application.study.update.interfaces.AnswerQuestionService
import br.all.application.study.update.interfaces.BatchAnswerQuestionPresenter
import br.all.application.study.util.TestDataFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class BatchAnswerQuestionServiceImplTest {

    @MockK(relaxed = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK
    private lateinit var questionRepository: QuestionRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK
    private lateinit var collaborationRepository: CollaborationRepository

    @MockK(relaxed = true)
    private lateinit var presenter: BatchAnswerQuestionPresenter

    private lateinit var sut: BatchAnswerQuestionServiceImpl
    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew
    private lateinit var questionId: UUID

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            collaborationRepository,
            factory.researcherId,
            factory.systematicStudyId,
            UUID.randomUUID()
        )
        sut = BatchAnswerQuestionServiceImpl(
            studyReviewRepository,
            questionRepository,
            systematicStudyRepository,
            credentialsService,
            collaborationRepository,
        )
        questionId = UUID.randomUUID()
    }

    @Nested
    @DisplayName("When successfully answering questions")
    inner class WhenSuccessfullyAnsweringQuestions {

        @Test
        fun `should successfully answer multiple questions of different types`() {
            val reviewDto = factory.generateDto()
            val context = "EXTRACTION"

            val textualQId = UUID.randomUUID()
            val textualQDto = factory.generateQuestionTextualDto(textualQId, factory.systematicStudyId, questionContext = context)
            val textualAnswer = factory.answerDetail(questionId = textualQId, type = "TEXTUAL", answer = "Valid textual answer")

            val numberedQId = UUID.randomUUID()
            val numberedQDto = factory.generateQuestionNumberedScaleDto(numberedQId, factory.systematicStudyId, questionContext = context, higher = 5, lower = 1)
            val numberedAnswer = factory.answerDetail(questionId = numberedQId, type = "NUMBERED_SCALE", answer = 5)

            val pickListQId = UUID.randomUUID()
            val pickListQDto = factory.generateQuestionPickListDto(pickListQId, factory.systematicStudyId, options = listOf("A", "B"), questionContext = context)
            val pickListAnswer = factory.answerDetail(questionId = pickListQId, type = "PICK_LIST", answer = "A")

            val request = factory.batchAnswerRequest(listOf(textualAnswer, numberedAnswer, pickListAnswer))

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns reviewDto
            every { questionRepository.findById(request.systematicStudyId, textualQId) } returns textualQDto
            every { questionRepository.findById(request.systematicStudyId, numberedQId) } returns numberedQDto
            every { questionRepository.findById(request.systematicStudyId, pickListQId) } returns pickListQDto

            sut.batchAnswerQuestion(presenter, request, context)

            verify(exactly = 1) { studyReviewRepository.saveOrUpdate(any()) }
            verify(exactly = 1) {
                presenter.prepareSuccessView(withArg { response ->
                    assertEquals(3, response.succeededAnswers.size)
                    assertTrue(response.failedAnswers.isEmpty())
                    assertEquals(3, response.totalAnswered)
                    assertTrue(response.succeededAnswers.containsAll(listOf(textualQId, numberedQId, pickListQId)))
                })
            }
        }

        @Test
        fun `should handle a mix of successful and failed answers`() {
            val reviewDto = factory.generateDto()
            val context = "ROB"

            val successQId = UUID.randomUUID()
            val successQDto = factory.generateQuestionTextualDto(successQId, factory.systematicStudyId, questionContext = context)
            val successAnswer = factory.answerDetail(questionId = successQId, type = "TEXTUAL", answer = "This will work")

            val notFoundQId = UUID.randomUUID()
            val notFoundAnswer = factory.answerDetail(questionId = notFoundQId, type = "TEXTUAL", answer = "This will fail")

            val wrongContextQId = UUID.randomUUID()
            val wrongContextQDto = factory.generateQuestionTextualDto(wrongContextQId, factory.systematicStudyId, questionContext = "EXTRACTION")
            val wrongContextAnswer = factory.answerDetail(questionId = wrongContextQId, type = "TEXTUAL", answer = "Wrong context")

            val request = factory.batchAnswerRequest(listOf(successAnswer, notFoundAnswer, wrongContextAnswer))

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns reviewDto
            every { questionRepository.findById(request.systematicStudyId, successQId) } returns successQDto
            every { questionRepository.findById(request.systematicStudyId, notFoundQId) } returns null
            every { questionRepository.findById(request.systematicStudyId, wrongContextQId) } returns wrongContextQDto

            sut.batchAnswerQuestion(presenter, request, context)

            verify(exactly = 1) { studyReviewRepository.saveOrUpdate(any()) }
            verify(exactly = 1) {
                presenter.prepareSuccessView(withArg { response ->
                    assertEquals(1, response.succeededAnswers.size)
                    assertEquals(2, response.failedAnswers.size)
                    assertEquals(1, response.totalAnswered)
                    assertTrue(response.succeededAnswers.contains(successQId))
                    assertTrue(response.failedAnswers.any { it.questionId == notFoundQId })
                    assertTrue(response.failedAnswers.any { it.questionId == wrongContextQId })
                })
            }
        }
    }

    @Nested
    @DisplayName("When failing to answer questions")
    inner class WhenFailingToAnswerQuestions {
        @Test
        fun `should create a failed answer entry for a question with conflicting types`() {
            val reviewDto = factory.generateDto()
            val context = "ROB"
            val questionId = UUID.randomUUID()
            val questionDto = factory.generateQuestionTextualDto(questionId, factory.systematicStudyId, questionContext = context)

            val answerDetail = factory.answerDetail(questionId, "PICK_LIST", "Some answer")
            val request = factory.batchAnswerRequest(listOf(answerDetail))

            preconditionCheckerMocking.makeEverythingWork()
            every { studyReviewRepository.findById(any(), any()) } returns reviewDto
            every { questionRepository.findById(any(), questionId) } returns questionDto

            sut.batchAnswerQuestion(presenter, request, context)

            verify(exactly = 1) {
                presenter.prepareSuccessView(withArg { response ->
                    assertTrue(response.succeededAnswers.isEmpty())
                    assertEquals(1, response.failedAnswers.size)
                    assertEquals(questionId, response.failedAnswers.first().questionId)
                    assertTrue("Type mismatch" in response.failedAnswers.first().reason)
                })
            }
        }

        @Test
        fun `should create a failed answer entry when answer value type is incompatible`() {
            val reviewDto = factory.generateDto()
            val context = "EXTRACTION"
            val questionId = UUID.randomUUID()
            val questionDto = factory.generateQuestionTextualDto(questionId, factory.systematicStudyId, questionContext = context)

            val answerDetail = factory.answerDetail(questionId, "TEXTUAL", 12345)
            val request = factory.batchAnswerRequest(listOf(answerDetail))

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(any(), any()) } returns reviewDto
            every { questionRepository.findById(any(), questionId) } returns questionDto

            sut.batchAnswerQuestion(presenter, request, context)

            verify(exactly = 1) {
                presenter.prepareSuccessView(withArg { response ->
                    assertTrue(response.succeededAnswers.isEmpty())
                    assertEquals(1, response.failedAnswers.size)
                    assertEquals(questionId, response.failedAnswers.first().questionId)
                    assertTrue("is not compatible with question type" in response.failedAnswers.first().reason)
                })
            }
        }

        @Test
        fun `should create a failed answer entry for unsupported labeled scale answer type`() {
            val reviewDto = factory.generateDto()
            val context = "ROB"
            val questionId = UUID.randomUUID()
            val labelDto = AnswerQuestionService.LabelDto(
                "LabelTest",
                1
            )
            val questionDto = factory.generateQuestionLabeledScaleDto(questionId, factory.systematicStudyId, questionContext = context, labelDto = labelDto)

            val answerDetail = factory.answerDetail(questionId, "LABELED_SCALE", "invalid answer format")
            val request = factory.batchAnswerRequest(listOf(answerDetail))

            preconditionCheckerMocking.makeEverythingWork()
            every { studyReviewRepository.findById(any(), any()) } returns reviewDto
            every { questionRepository.findById(any(), questionId) } returns questionDto

            sut.batchAnswerQuestion(presenter, request, context)

            verify(exactly = 1) {
                presenter.prepareSuccessView(withArg { response ->
                    assertTrue(response.succeededAnswers.isEmpty())
                    assertEquals(1, response.failedAnswers.size)
                    assertEquals(questionId, response.failedAnswers.first().questionId)
                    assertTrue("Unsupported answer type for 'LABELED_SCALE'" in response.failedAnswers.first().reason)
                })
            }
        }
    }
}