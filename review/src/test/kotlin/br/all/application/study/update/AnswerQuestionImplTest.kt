package br.all.application.study.update

import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.AnswerQuestionImpl
import br.all.application.study.update.interfaces.AnswerQuestionPresenter
import br.all.application.study.update.interfaces.AnswerQuestionService
import br.all.application.study.util.TestDataFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class AnswerQuestionImplTest {

    @MockK(relaxed = true) private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var questionRepository: QuestionRepository
    @MockK private lateinit var credentialService: CredentialsService
    @MockK(relaxed = true) private lateinit var presenter: AnswerQuestionPresenter

    private lateinit var sut: AnswerQuestionService

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMockingNew

    private lateinit var questionId: UUID

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMockingNew(
            presenter,
            credentialService,
            systematicStudyRepository,
            factory.researcherId,
            factory.systematicStudyId
        )
        sut = AnswerQuestionImpl(
            studyReviewRepository,
            questionRepository,
            systematicStudyRepository,
            credentialService,
        )
        questionId = UUID.randomUUID()
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully answering a question")
    inner class WhenSuccessfullyAnsweringAQuestion {
        @Test
        fun `should successfully answer a textual question`() {
            val dto = factory.generateDto()
            val questionDto = factory.generateQuestionTextualDto(questionId, factory.systematicStudyId, questionContext = "ROB")
            val request = factory.answerQuestionModel(questionId, "TEXTUAL", "Answer Test")
            val context = "ROB"

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            sut.answerQuestion(presenter, request, context)

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(any())
                presenter.prepareSuccessView(any())
            }
        }

        @Test
        fun `should successfully answer a labeled scale question`() {
            val dto = factory.generateDto()
            val answer = factory.questionLabelDto("Test Name", 1)
            val questionDto = factory.generateQuestionLabeledScaleDto(questionId, labelDto = answer, questionContext = "ROB")
            val request = factory.answerQuestionModel(questionId, "LABELED_SCALE", answer)
            val context = "ROB"

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            sut.answerQuestion(presenter, request, context)

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(any())
                presenter.prepareSuccessView(any())
            }
        }

        @Test
        fun `should successfully answer a numbered scale question`() {
            val dto = factory.generateDto()
            val answer = 9
            val questionDto = factory.generateQuestionNumberedScaleDto(
                questionId, factory.systematicStudyId, higher = 10, lower = 1, questionContext = "EXTRACTION"
            )
            val request = factory.answerQuestionModel(questionId, "NUMBERED_SCALE", answer)
            val context = "EXTRACTION"

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            sut.answerQuestion(presenter, request, context)

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(any())
                presenter.prepareSuccessView(any())
            }
        }

        @Test
        fun `should successfully answer a pick one question`() {
            val dto = factory.generateDto()
            val questionDto = factory.generateQuestionPickListDto(questionId, factory.systematicStudyId, options = listOf("op1", "op2", "op3"), questionContext = "EXTRACTION")
            val answer = "op1"
            val request = factory.answerQuestionModel(questionId, "PICK_LIST", answer)
            val context = "EXTRACTION"

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            sut.answerQuestion(presenter, request, context)

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(any())
            }
        }

        @Test
        fun `should handle labeled scale answer as LinkedHashMap`() {
            val dto = factory.generateDto()
            val labelDto = factory.questionLabelDto("Test Name", 1)
            val questionDto = factory.generateQuestionLabeledScaleDto(
                questionId,
                factory.systematicStudyId,
                questionContext = "ROB",
                labelDto = labelDto,
            )
            val request = factory.answerQuestionModel(
                questionId,
                "LABELED_SCALE",
                mapOf("name" to "Test Name", "value" to 1)
            )
            val context = "ROB"

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            sut.answerQuestion(presenter, request, context)

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(any())
                presenter.prepareSuccessView(any())
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to answer a question")
    inner class WhenFailingToAnswerAQuestion {
        @Test
        fun `should not work if study doesn't exist`() {
            val request = factory.answerQuestionModel(questionId, "TEXTUAL", "Answer")
            val context = "ROB"

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns null
            sut.answerQuestion(presenter, request, context)

            verify {
                presenter.prepareFailView(any<EntityNotFoundException>())
            }
        }

        @Test
        fun `should not work if question doesn't exist`() {
            val dto = factory.generateDto()
            val request = factory.answerQuestionModel(questionId, "TEXTUAL", "Testing")
            val context = "ROB"

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns null

            sut.answerQuestion(presenter, request, context)

            verify {
                presenter.prepareFailView(any<EntityNotFoundException>())
            }
        }

        @Test
        fun `should not answer when unauthorized`() {
            val request = factory.answerQuestionModel(questionId, "TEXTUAL", "failure test")
            val context = "ROB"

            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.answerQuestion(presenter, request, context)
            }
        }

        @Test
        fun `should not answer when unauthenticated`() {
            val request = factory.answerQuestionModel(questionId, "TEXTUAL", "nono")
            val context = "ROB"

            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.answerQuestion(presenter, request, context)
            }
        }

        @Test
        fun `should not answer when systematic study does not exist`() {
            val request = factory.answerQuestionModel(questionId, "TEXTUAL", "not real")
            val context = "ROB"

            preconditionCheckerMocking.testForNonexistentSystematicStudy(presenter, request) { _, _ ->
                sut.answerQuestion(presenter, request, context)
            }
        }

        @Test
        fun `should not answer question of diferent context`() {
            val dto = factory.generateDto()
            val questionDto = factory.generateQuestionTextualDto(questionId, factory.systematicStudyId, questionContext = "ROB")
            val request = factory.answerQuestionModel(questionId, "TEXTUAL", "Answer Test")
            val context = "EXTRACTION"

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            sut.answerQuestion(presenter, request, context)

            verify {
                presenter.prepareFailView(any<IllegalArgumentException>())
            }
        }

        @Test
        fun `should not answer question with null context`() {
            val dto = factory.generateDto()
            val questionDto = factory.generateQuestionTextualDto(questionId, factory.systematicStudyId, questionContext = "ROB")
            val request = factory.answerQuestionModel(questionId, "TEXTUAL", "Answer Test")
            val context = null

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            sut.answerQuestion(presenter, request, context)

            verify {
                presenter.prepareFailView(any<IllegalArgumentException>())
            }
        }

        @Test
        fun `should not answer question with conflicting types`() {
            val dto = factory.generateDto()
            val questionDto = factory.generateQuestionTextualDto(questionId, factory.systematicStudyId, questionContext = "ROB")
            val request = factory.answerQuestionModel(questionId, "PICK_LIST", "Answer Test")
            val context = "ROB"

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            assertThrows<IllegalArgumentException> {
                sut.answerQuestion(presenter, request, context)
            }
        }

        @Test
        fun `should not answer labeled scale if answer is of unsupported type`() {
            val dto = factory.generateDto()
            val labelDto = factory.questionLabelDto("Test Name", 1)
            val questionDto = factory.generateQuestionLabeledScaleDto(questionId, labelDto = labelDto, questionContext = "ROB")
            val answer = "wrong answer"
            val request = factory.answerQuestionModel(questionId, "LABELED_SCALE", answer)
            val context = "ROB"

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            assertThrows<IllegalArgumentException> {
                sut.answerQuestion(presenter, request, context)
            }
        }

        @Test
        fun `should not answer question if answer if of unsupported type`() {
            val dto = factory.generateDto()
            val questionDto =
                factory.generateQuestionTextualDto(questionId, factory.systematicStudyId, questionContext = "ROB")
            val request = factory.answerQuestionModel(questionId, "TEXTUAL", 1)
            val context = "ROB"

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto
            assertThrows<IllegalArgumentException> {
                sut.answerQuestion(presenter, request, context)
            }
        }
    }
}
