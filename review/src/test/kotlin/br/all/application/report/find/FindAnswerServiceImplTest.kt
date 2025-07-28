package br.all.application.report.find

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.question.repository.QuestionRepository
import br.all.application.report.find.presenter.FindAnswerPresenter
import br.all.application.report.find.service.FindAnswerService
import br.all.application.report.find.service.FindAnswerServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
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
import br.all.application.study.util.TestDataFactory as StudyReviewFactory
import br.all.application.report.util.TestDataFactory as ReportFactory

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindAnswerServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var questionRepository: QuestionRepository

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: FindAnswerPresenter

    @MockK
    private lateinit var collaborationRepository: CollaborationRepository

    @InjectMockKs
    private lateinit var sut: FindAnswerServiceImpl

    private lateinit var precondition: PreconditionCheckerMockingNew
    private lateinit var studyFactory: StudyReviewFactory
    private lateinit var reportFactory: ReportFactory

    private lateinit var researcherId: UUID
    private lateinit var systematicStudyId: UUID
    private lateinit var questionId: UUID

    @BeforeEach
    fun setup() {
        studyFactory = StudyReviewFactory()
        reportFactory = ReportFactory()

        researcherId = studyFactory.researcherId
        systematicStudyId = studyFactory.systematicStudyId
        questionId = reportFactory.questionId

        precondition = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            collaborationRepository,
            researcherId,
            systematicStudyId,
            UUID.randomUUID()
        )
        precondition.makeEverythingWork()
    }

    @Nested
    @DisplayName("When successfully finding answers")
    inner class SuccessfullyFindingAnswers {
        @Test
        fun `should return answers grouped by answer value with associated study IDs`() {
            val question = reportFactory.questionDto(
                questionId = questionId,
                systematicStudyId = systematicStudyId
            )

            val questionAnswers = listOf(
                reportFactory.answerDto(studyReviewId = 1L, answer = "Yes"),
                reportFactory.answerDto(studyReviewId = 2L, answer = "Yes"),
                reportFactory.answerDto(studyReviewId = 3L, answer = "No"),
                reportFactory.answerDto(studyReviewId = 4L, answer = "Maybe"),
                reportFactory.answerDto(studyReviewId = 5L, answer = "Yes")
            )

            every { questionRepository.findById(systematicStudyId, questionId) } returns question
            every { studyReviewRepository.findAllQuestionAnswers(systematicStudyId, questionId) } returns questionAnswers

            val request = FindAnswerService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                questionId = questionId
            )

            sut.find(presenter, request)

            val expectedAnswerMap = mapOf(
                "Maybe" to listOf(4L),
                "No" to listOf(3L),
                "Yes" to listOf(1L, 2L, 5L)
            )

            val expectedResponse = FindAnswerService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                question = question,
                answer = expectedAnswerMap
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }

        @Test
        fun `should handle numeric answers with proper sorting`() {
            val question = reportFactory.questionDto(
                questionId = questionId,
                systematicStudyId = systematicStudyId,
                questionType = "NUMBERED_SCALE"
            )

            val questionAnswers = listOf(
                reportFactory.answerDto(studyReviewId = 2L, answer = "10"),
                reportFactory.answerDto(studyReviewId = 3L, answer = "2"),
                reportFactory.answerDto(studyReviewId = 4L, answer = "Text"),
                reportFactory.answerDto(studyReviewId = 1L, answer = "1")
            )

            every { questionRepository.findById(systematicStudyId, questionId) } returns question
            every { studyReviewRepository.findAllQuestionAnswers(systematicStudyId, questionId) } returns questionAnswers

            val request = FindAnswerService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                questionId = questionId
            )

            sut.find(presenter, request)

            val expectedAnswerMap = mapOf(
                "1" to listOf(1L),
                "2" to listOf(3L),
                "10" to listOf(2L),
                "Text" to listOf(4L)
            )

            val expectedResponse = FindAnswerService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                question = question,
                answer = expectedAnswerMap
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }

        @Test
        fun `should return empty results when no answers exist`() {
            val question = reportFactory.questionDto(
                questionId = questionId,
                systematicStudyId = systematicStudyId
            )

            every { questionRepository.findById(systematicStudyId, questionId) } returns question
            every { studyReviewRepository.findAllQuestionAnswers(systematicStudyId, questionId) } returns emptyList()

            val request = FindAnswerService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                questionId = questionId
            )

            sut.find(presenter, request)

            val expectedResponse = FindAnswerService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                question = question,
                answer = emptyMap()
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }
    }
}