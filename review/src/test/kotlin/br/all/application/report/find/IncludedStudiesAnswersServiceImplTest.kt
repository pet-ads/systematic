package br.all.application.report.find

import br.all.application.question.repository.QuestionDto
import br.all.application.question.repository.QuestionRepository
import br.all.application.report.find.presenter.IncludedStudiesAnswersPresenter
import br.all.application.report.find.service.IncludedStudiesAnswersServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.util.TestDataFactory as StudyTestDataFactory
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import br.all.application.report.find.service.IncludedStudiesAnswersService.RequestModel
import br.all.application.report.util.TestDataFactory
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.domain.model.question.QuestionContextEnum
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class IncludedStudiesAnswersServiceImplTest {

    @MockK(relaxUnitFun = true)
    lateinit var presenter: IncludedStudiesAnswersPresenter

    @MockK(relaxUnitFun = true)
    lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    lateinit var questionRepository: QuestionRepository

    @MockK(relaxUnitFun = true)
    lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    lateinit var credentialsService: CredentialsService

    @InjectMockKs
    lateinit var sut: IncludedStudiesAnswersServiceImpl

    private lateinit var precondition: PreconditionCheckerMockingNew
    private lateinit var studyFactory: StudyTestDataFactory
    private lateinit var testDataFactory: TestDataFactory
    private lateinit var protocolDtoFactory: br.all.application.protocol.util.TestDataFactory

    private lateinit var researcherId: UUID
    private lateinit var systematicStudyId: UUID

    @BeforeEach
    fun setUp() {
        studyFactory = StudyTestDataFactory()
        testDataFactory = TestDataFactory()
        protocolDtoFactory = br.all.application.protocol.util.TestDataFactory()

        researcherId = studyFactory.researcherId
        systematicStudyId = studyFactory.systematicStudyId


        precondition = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            researcherId,
            systematicStudyId
        )

        precondition.makeEverythingWork()
    }

    @DisplayName("When being successful")
    @Nested
    inner class WhenBeingSuccessful {
        @Test
        fun `should return answers correctly for one answer`() {
            val studyReviewId = studyFactory.studyReviewId
            val question1 = testDataFactory.questionDto(questionContext = QuestionContextEnum.ROB)
            val question2 = testDataFactory.questionDto(questionContext = QuestionContextEnum.EXTRACTION)

            val studyReview = studyFactory.generateDto(
                studyReviewId = studyReviewId,
                formAnswers = mapOf(Pair(question2.questionId, "manga com leite mata sim!")),
                robAnswers = mapOf(Pair(question1.questionId, "não é o pedro santos :(")),
            )

            every { studyReviewRepository.findById(systematicStudyId, studyReviewId) } returns studyReview

            every {
                questionRepository.findAllBySystematicStudyId(systematicStudyId, any())
            } answers {
                when (secondArg<QuestionContextEnum>()) {
                    QuestionContextEnum.ROB -> listOf(question1)
                    QuestionContextEnum.EXTRACTION -> listOf(question2)
                    else -> emptyList()
                }
            }

            val request = RequestModel(researcherId, systematicStudyId, studyReviewId)

            sut.findAnswers(presenter, request)

            val expectedAnswer = testDataFactory.expectedIncludedStudiesResponse(
                studyReviewDto = studyReview,
                userId = researcherId,
                extractionQuestions = listOf(question2),
                robQuestions = listOf(question1),
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedAnswer)
            }
        }

        @Test
        fun `should return answers correctly for multiple answers`() {
            val studyReviewId = studyFactory.studyReviewId

            val extractionQuestions: List<QuestionDto> = List(10) {
                testDataFactory.questionDto(questionContext = QuestionContextEnum.EXTRACTION)
            }
            val robQuestions: List<QuestionDto> = List(10) {
                testDataFactory.questionDto(questionContext = QuestionContextEnum.ROB)
            }

            val studyReview = studyFactory.generateDto(
                studyReviewId = studyReviewId,
                formAnswers = extractionQuestions.associate { it.questionId to "ANSWER TO ${it.description}" },
                robAnswers = robQuestions.associate { it.questionId to "ANSWER TO ${it.description}" },
            )

            every { studyReviewRepository.findById(systematicStudyId, studyReviewId) } returns studyReview

            every {
                questionRepository.findAllBySystematicStudyId(systematicStudyId, any())
            } answers {
                when (secondArg<QuestionContextEnum>()) {
                    QuestionContextEnum.ROB -> robQuestions
                    QuestionContextEnum.EXTRACTION -> extractionQuestions
                    else -> emptyList()
                }
            }

            val request = RequestModel(researcherId, systematicStudyId, studyReviewId)

            sut.findAnswers(presenter, request)

            val expectedAnswer = testDataFactory.expectedIncludedStudiesResponse(
                studyReviewDto = studyReview,
                userId = researcherId,
                extractionQuestions = extractionQuestions,
                robQuestions = robQuestions,
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedAnswer)
            }
        }
        @Test
        fun `should return nothing when study does not have any recorded answer`() {
            val studyReviewId = studyFactory.studyReviewId

            val extractionQuestions: List<QuestionDto> = List(10) {
                testDataFactory.questionDto(questionContext = QuestionContextEnum.EXTRACTION)
            }
            val robQuestions: List<QuestionDto> = List(10) {
                testDataFactory.questionDto(questionContext = QuestionContextEnum.ROB)
            }

            val studyReview = studyFactory.generateDto(
                studyReviewId = studyReviewId,
                formAnswers = emptyMap(),
                robAnswers = emptyMap(),
            )

            every { studyReviewRepository.findById(systematicStudyId, studyReviewId) } returns studyReview

            every {
                questionRepository.findAllBySystematicStudyId(systematicStudyId, any())
            } answers {
                when (secondArg<QuestionContextEnum>()) {
                    QuestionContextEnum.ROB -> robQuestions
                    QuestionContextEnum.EXTRACTION -> extractionQuestions
                    else -> emptyList()
                }
            }

            val request = RequestModel(researcherId, systematicStudyId, studyReviewId)

            sut.findAnswers(presenter, request)

            verify(exactly = 1) {
                presenter.prepareSuccessView(any())
            }
        }
    }
    @DisplayName("When being successful")
    @Nested
    inner class WhenBeingUnsuccessful {
        @Test
        fun `should fail view when there is no study review`() {
            val studyReviewId = studyFactory.studyReviewId
            val extractionQuestions: List<QuestionDto> = List(10) {
                testDataFactory.questionDto(questionContext = QuestionContextEnum.EXTRACTION)
            }
            val robQuestions: List<QuestionDto> = List(10) {
                testDataFactory.questionDto(questionContext = QuestionContextEnum.ROB)
            }

            every { studyReviewRepository.findById(systematicStudyId, studyReviewId) } returns null

            every {
                questionRepository.findAllBySystematicStudyId(systematicStudyId, any())
            } answers {
                when (secondArg<QuestionContextEnum>()) {
                    QuestionContextEnum.ROB -> robQuestions
                    QuestionContextEnum.EXTRACTION -> extractionQuestions
                    else -> emptyList()
                }
            }

            val request = RequestModel(researcherId, systematicStudyId, studyReviewId)

            sut.findAnswers(presenter, request)

            val slot = slot<EntityNotFoundException>()

            verify(exactly = 1) {
                presenter.prepareFailView(capture(slot))
            }

            assertTrue { slot.captured.message?.
            contains("StudyReview not found for id ${request.systematicStudyId}") == true }
        }

        @Test
        fun `should fail when there is no user`() {
            precondition.makeUserUnauthenticated()
            val studyReviewId = studyFactory.studyReviewId
            val extractionQuestions: List<QuestionDto> = List(10) {
                testDataFactory.questionDto(questionContext = QuestionContextEnum.EXTRACTION)
            }
            val robQuestions: List<QuestionDto> = List(10) {
                testDataFactory.questionDto(questionContext = QuestionContextEnum.ROB)
            }

            val studyReview = studyFactory.generateDto(
                studyReviewId = studyReviewId,
                formAnswers = extractionQuestions.associate { it.questionId to "ANSWER TO ${it.description}" },
                robAnswers = robQuestions.associate { it.questionId to "ANSWER TO ${it.description}" },
            )

            every { studyReviewRepository.findById(systematicStudyId, studyReviewId) } returns studyReview

            every {
                questionRepository.findAllBySystematicStudyId(systematicStudyId, any())
            } answers {
                when (secondArg<QuestionContextEnum>()) {
                    QuestionContextEnum.ROB -> robQuestions
                    QuestionContextEnum.EXTRACTION -> extractionQuestions
                    else -> emptyList()
                }
            }

            every {
                credentialsService.loadCredentials(userId = researcherId)
            } returns null

            val request = RequestModel(researcherId, systematicStudyId, studyReviewId)

            sut.findAnswers(presenter, request)

            verify(exactly = 1) {
                presenter.prepareIfFailsPreconditions(any(), any())
            }

            assertEquals(true, presenter.isDone())
        }

        @Test
        fun `should fail when there is no study review`() {
            precondition.makeSystematicStudyNonexistent()

            val studyReviewId = studyFactory.studyReviewId
            val question1 = testDataFactory.questionDto(questionContext = QuestionContextEnum.ROB)
            val question2 = testDataFactory.questionDto(questionContext = QuestionContextEnum.EXTRACTION)

            val studyReview = studyFactory.generateDto(
                studyReviewId = studyReviewId,
                formAnswers = mapOf(Pair(question2.questionId, "manga com leite mata sim!")),
                robAnswers = mapOf(Pair(question1.questionId, "não é o pedro santos :(")),
            )

            every { studyReviewRepository.findById(systematicStudyId, studyReviewId) } returns studyReview

            every {
                questionRepository.findAllBySystematicStudyId(systematicStudyId, any())
            } answers {
                when (secondArg<QuestionContextEnum>()) {
                    QuestionContextEnum.ROB -> listOf(question1)
                    QuestionContextEnum.EXTRACTION -> listOf(question2)
                    else -> emptyList()
                }
            }

            val request = RequestModel(researcherId, systematicStudyId, studyReviewId)

            sut.findAnswers(presenter, request)

            verify(exactly = 1) {
                presenter.prepareIfFailsPreconditions(any(), any())
            }

            assertEquals(true, presenter.isDone())
        }
    }
}