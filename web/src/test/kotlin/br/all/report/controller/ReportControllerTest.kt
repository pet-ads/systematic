package br.all.report.controller

import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.question.QuestionDocument
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.review.SystematicStudyDocument
import br.all.infrastructure.study.MongoStudyReviewRepository
import br.all.infrastructure.study.StudyReviewDocument
import br.all.report.shared.TestDataFactory
import br.all.security.service.ApplicationUser
import br.all.shared.TestHelperService
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*
import br.all.review.shared.TestDataFactory as SystematicStudyTestDataFactory
import br.all.study.utils.TestDataFactory as StudyReviewTestDataFactory


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
    private lateinit var faker: Faker
    private lateinit var systematicStudy: SystematicStudyDocument
    private lateinit var studyReview: StudyReviewDocument
    private lateinit var robQuestions: List<QuestionDocument>
    private lateinit var factory: TestDataFactory
    private lateinit var studyReviewDataFactory: StudyReviewTestDataFactory
    private lateinit var systematicStudyDataFactory: SystematicStudyTestDataFactory

    @BeforeEach
    fun setUp() {
        faker = Faker()
        factory = TestDataFactory()
        systematicStudyDataFactory = SystematicStudyTestDataFactory()
        studyReviewDataFactory = StudyReviewTestDataFactory()
        user = testHelperService.createApplicationUser()

        systematicStudy = systematicStudyDataFactory.createSystematicStudyDocument(
            collaborators = mutableSetOf(user.id)
        )

        robQuestions = factory.createRobQuestions(
            systematicStudy.id,
            questionRepository,
        )

        systematicStudyRepository.save(systematicStudy)
    }

    @AfterEach
    fun tearDown() {
        systematicStudyRepository.deleteAll()
        factory.deleteRobQuestions(questionRepository)
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
                studyReview = studyReviewDataFactory.reviewDocument(
                    systematicStudy.id,
                    studyReviewId = 11111,
                    robAnswers = robQuestions[0].let { mapOf(it.questionId to "resposta ${it.description}") }
                )

                studyReviewRepository.save(studyReview)

                val expected = factory.expectedJson(
                    userId = user.id,
                    question = robQuestions[0],
                    review = studyReview,
                )

                mockMvc.perform(
                    get(getUrl(questionId = robQuestions[0].questionId))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isOk)
                    .andExpect(
                        content().json(
                            expected
                        )
                    )
            }
            @Test
            fun `should find answer of numbered scale question and return 200`() {
                val randomNumber = faker.random.nextInt(1, 10)
                studyReview = studyReviewDataFactory.reviewDocument(
                    systematicStudy.id,
                    studyReviewId = 11111,
                    robAnswers = mapOf(robQuestions[1].questionId to "$randomNumber")
                )

                studyReviewRepository.save(studyReview)

                val expected = factory.expectedJson(
                    userId = user.id,
                    question = robQuestions[1],
                    review = studyReview,
                )

                mockMvc.perform(
                    get(getUrl(questionId = robQuestions[1].questionId))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isOk)
                    .andExpect(
                        content().json(
                            expected
                        )
                    )
            }

            @Test
            fun `should find answer of labeled scale question and return 200`() {
                val scaleMap = robQuestions[2].scales!!

                val (key, value) = scaleMap.entries.first()

                val labelString = "Label(name: $key, value: $value)"
                studyReview = studyReviewDataFactory.reviewDocument(
                    systematicStudy.id,
                    studyReviewId = 11111,
                    robAnswers = mapOf(
                        robQuestions[2].questionId to labelString
                    )
                )
                studyReviewRepository.save(studyReview)

                val expected = factory.expectedJson(
                    userId = user.id,
                    question = robQuestions[2],
                    review = studyReview,
                )

                mockMvc.perform(
                    get(getUrl(questionId = robQuestions[2].questionId))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isOk)
                    .andExpect(
                        content().json(
                            expected
                        )
                    )
            }
        }
    }
}