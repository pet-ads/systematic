package br.all.report.controller

import br.all.domain.model.question.QuestionContextEnum
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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
    private lateinit var extractionQuestions: List<QuestionDocument>
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

        robQuestions = factory.createQuestions(
            systematicStudy.id,
            questionRepository,
            QuestionContextEnum.ROB
        )

        extractionQuestions = factory.createQuestions(
            systematicStudy.id,
            questionRepository,
            QuestionContextEnum.EXTRACTION
        )

        val (robKey, robValue) = robQuestions[2].scales!!.entries.first()
        val (extractionKey, extractionValue) = extractionQuestions[2].scales!!.entries.first()
        val extractionOptions = extractionQuestions[3].options!!
        val robOptions = robQuestions[3].options!!

        studyReview = studyReviewDataFactory.reviewDocument(
            systematicStudy.id,
            studyReviewId = 11111,
            robAnswers = mapOf(
                robQuestions[0].questionId to "Resposta: ${robQuestions[0].description}",
                robQuestions[1].questionId to "2",
                robQuestions[2].questionId to "Label(name: $robKey, value: $robValue)",
                robQuestions[3].questionId to robOptions.first()
            ),
            formAnswers = mapOf(
                extractionQuestions[0].questionId to "Resposta: ${extractionQuestions[0].description}",
                extractionQuestions[1].questionId to "2",
                extractionQuestions[2].questionId to "Label(name: $extractionKey, value: $extractionValue)",
                extractionQuestions[3].questionId to extractionOptions.first(),
            )
        )

        studyReviewRepository.save(studyReview)

        systematicStudyRepository.save(systematicStudy)
    }

    @AfterEach
    fun tearDown() {
        studyReviewRepository.deleteAll()
        systematicStudyRepository.deleteAll()
        factory.deleteQuestions(questionRepository)
        testHelperService.deleteApplicationUser(user.id)
    }

    private fun getUrl(systematicStudy: UUID = this.systematicStudy.id, questionId: UUID) =
        "/api/v1/systematic-study/$systematicStudy/report/find-answer/$questionId"

    @Nested
    @DisplayName("When searching answers of questions")
    inner class WhenFindingAnswersOfQuestions {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And finding them")
        inner class AndFindingThem {

            @ParameterizedTest(name = "Q#{0} [{1}] should return 200 and correct response for ROB questions")
            @ValueSource(ints = [0, 1, 2, 3])
            fun `should find answer for all rob questions`(index: Int) {
                val question = robQuestions[index]

                val expected = factory.expectedJson(
                    userId = user.id,
                    question = question,
                    review = studyReview,
                )

                mockMvc.perform(
                    get(getUrl(questionId = question.questionId))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isOk)
                    .andExpect(
                        content().json(
                            expected
                        )
                    )
            }

            @ParameterizedTest(name = "Q#{0} should return 200 and correct response for EXTRACTION questions")
            @ValueSource(ints = [0, 1, 2, 3])
            fun `should find answer for all EXTRACTION questions`(index: Int) {
                val question = extractionQuestions[index]

                val expected = factory.expectedJson(
                    userId = user.id,
                    question = question,
                    review = studyReview,
                )

                mockMvc.perform(
                    get(getUrl(questionId = question.questionId))
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
            fun `should return 200 if the question does not have answers`() {
                val notAnsweredQuestions = factory.createQuestions(
                    systematicStudy.id,
                    questionRepository,
                    QuestionContextEnum.ROB
                )

                mockMvc.perform(
                    get(getUrl(questionId = notAnsweredQuestions[0].questionId))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.answer").isMap)
                    .andExpect(jsonPath("$.answer").isEmpty)
            }

            @Test
            fun `should not allow unauthenticated user to find questions`() {
                testHelperService.testForUnauthenticatedUser(
                    mockMvc = mockMvc,
                    requestBuilder = get(getUrl(questionId = robQuestions[1].questionId))
                )
            }

            @Test
            fun `should not allow unauthorized users to find questions`() {
                testHelperService.testForUnauthorizedUser(
                    mockMvc = mockMvc,
                    requestBuilder = get(getUrl(questionId = robQuestions[1].questionId))
                )
            }
        }
        @Nested
        @DisplayName("And not finding them")
        inner class AndNotFindingThem {
            @Test
            fun `should return 404 if the question does not exist`() {
                val question = UUID.randomUUID()

                mockMvc.perform(
                    get(getUrl(questionId = question))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isNotFound)
            }
        }
    }
}