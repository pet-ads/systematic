package br.all.application.study.update

import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.AnswerRiskOfBiasQuestionImpl
import br.all.application.study.update.interfaces.AnswerRiskOfBiasQuestionPresenter
import br.all.application.study.util.TestDataFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertFailsWith

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class AnswerRiskOfBiasQuestionImplTest {

    @MockK(relaxed = true) private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK private lateinit var questionRepository: QuestionRepository
    @MockK private lateinit var credentialService: CredentialsService
    @MockK(relaxed = true) private lateinit var presenter: AnswerRiskOfBiasQuestionPresenter

    private lateinit var sut: AnswerRiskOfBiasQuestionImpl

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
        sut = AnswerRiskOfBiasQuestionImpl(
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
        fun `should successfully Answer a text question`() {
            val dto = factory.generateDto()
            val questionDto = factory.generateQuestionTextualDto(questionId, factory.systematicStudyId)
            val request = factory.answerRobRequestModel(questionId, "TEXTUAL", "Answer Test")

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            sut.answerRobQuestion(presenter, request)

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(any())
                presenter.prepareSuccessView(any())
            }
        }

        @Test
        fun `should successfully Answer a labeled scale question`() {
            val dto = factory.generateDto()
            val answer = factory.labelRobDto("Test Name", 1)
            val questionDto = factory.generateQuestionLabeledScaleDto(questionId, labelDto = answer)
            val request = factory.answerRobRequestModel(questionId, "LABELED_SCALE", answer)

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            sut.answerRobQuestion(presenter, request)

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
        fun `should not be able to answer question with mismatched type`() {
            val dto = factory.generateDto()
            val questionDto = factory.generateQuestionTextualDto(questionId, factory.systematicStudyId)
            val request = factory.answerRobRequestModel(questionId, "LABELED_SCALE", "Test")

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            assertFailsWith<IllegalArgumentException> {
                sut.answerRobQuestion(presenter, request)
            }
        }

        @Test
        fun `should not work if study doesn't exist`() {
            val request = factory.answerRobRequestModel(questionId, "TEXTUAL", "Answer")

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns null
            sut.answerRobQuestion(presenter, request)

            verify {
                presenter.prepareFailView(any<EntityNotFoundException>())
            }
        }

        @Test
        fun `should not work if question doesn't exist`() {
            val dto = factory.generateDto()
            val request = factory.answerRobRequestModel(questionId, "TEXTUAL", "Testing")

            preconditionCheckerMocking.makeEverythingWork()

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns null

            sut.answerRobQuestion(presenter, request)

            verify {
                presenter.prepareFailView(any<EntityNotFoundException>())
            }
        }

        @Test
        fun `should not answer when unauthorized`() {
            val request = factory.answerRobRequestModel(questionId, "TEXTUAL", "failure test")

            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.answerRobQuestion(presenter, request)
            }
        }

        @Test
        fun `should not answer when unauthenticated`() {
            val request = factory.answerRobRequestModel(questionId, "TEXTUAL", "nono")

            preconditionCheckerMocking.testForUnauthenticatedUser(presenter, request) { _, _ ->
                sut.answerRobQuestion(presenter, request)
            }

        }

        @Test
        fun `should not answer when systematic study does not exist`() {
            val request = factory.answerRobRequestModel(questionId, "TEXTUAL", "not real")

            preconditionCheckerMocking.testForNonexistentSystematicStudy(presenter, request) { _, _ ->
                sut.answerRobQuestion(presenter, request)
            }

        }
    }

}