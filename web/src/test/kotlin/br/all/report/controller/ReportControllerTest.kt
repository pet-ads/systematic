package br.all.report.controller

import br.all.domain.model.question.QuestionContextEnum
import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.review.SystematicStudyDocument
import br.all.infrastructure.study.MongoStudyReviewRepository
import br.all.report.shared.TestDataFactory
import br.all.security.service.ApplicationUser
import br.all.shared.TestHelperService
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import br.all.review.shared.TestDataFactory as SystematicStudyTestDataFactory
import java.util.*


@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
@DisplayName("Report Controller Integration Tests")
class ReportControllerTest(
    @Autowired private val studyReviewRepository: MongoStudyReviewRepository,
    @Autowired private val systematicStudyRepository: MongoSystematicStudyRepository,
    @Autowired private val questionRepository: MongoQuestionRepository,
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val testHelperService: TestHelperService
) {
    private lateinit var user: ApplicationUser
    private lateinit var systematicStudy: SystematicStudyDocument
    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyDataFactory: SystematicStudyTestDataFactory

    @BeforeEach
    fun setUp()  {
        factory = TestDataFactory()
        systematicStudyDataFactory = SystematicStudyTestDataFactory()

        user = testHelperService.createApplicationUser()

        systematicStudy = systematicStudyDataFactory.createSystematicStudyDocument(
            collaborators = mutableSetOf(user.id)
        )

        val rq1 = factory.validCreateTextualQuestionDocument(
            questionId = UUID.randomUUID(),
            systematicStudyId = systematicStudy.id,
            questionType = QuestionContextEnum.ROB
        )

        val rq2 = factory.validCreateNumberedScaleQuestionDocument(
            questionId = UUID.randomUUID(),
            systematicStudyId = systematicStudy.id,
            questionType = QuestionContextEnum.ROB
        )

        val rq3 = factory.validCreateLabeledScaleQuestionDocument(
            questionId = UUID.randomUUID(),
            systematicStudyId = systematicStudy.id,
            questionType = QuestionContextEnum.ROB
        )

        val rq4 = factory.validCreatePickListQuestionDocument(
            questionId = UUID.randomUUID(),
            systematicStudyId = systematicStudy.id,
            questionType = QuestionContextEnum.ROB
        )

        questionRepository.saveAll(listOf(rq1, rq2, rq3, rq4))
        systematicStudyRepository.save(systematicStudy)
    }

    @AfterEach
    fun tearDown() {
        systematicStudyRepository.deleteAll()
        testHelperService.deleteApplicationUser(user.id)
    }

    private fun getUrl(systematicStudy: UUID = this.systematicStudy.id, questionId: UUID) =
        "/api/v1/systematic-study/$systematicStudy/report/find-answer/$questionId"

    @Nested
    @DisplayName("When finding answers of rob questions")
    inner class WhenFindingAnswersOfRobQuestions {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And finding them")
        inner class AndFindingThem {
            @Test
            fun `should find answer of textual question and return 200`() {
                val questions = questionRepository.findAllBySystematicStudyId(systematicStudy.id)

                val answer: Pair<UUID, String> = Pair(questions[0].questionId, "resposta rq1")

                factory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    formAnswers = emptyMap(),
                    robAnswers = mapOf(answer)
                )

                mockMvc.perform(
                    get(getUrl(questionId = questions[0].questionId))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
            }
        }
    }
}