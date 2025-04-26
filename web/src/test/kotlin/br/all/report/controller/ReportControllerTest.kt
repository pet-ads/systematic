package br.all.report.controller

import br.all.domain.model.question.QuestionContextEnum
import br.all.infrastructure.protocol.MongoProtocolRepository
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
import br.all.protocol.shared.TestDataFactory as ProtocolTestDataFactory


@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
@DisplayName("Report Controller Integration Tests")
class ReportControllerTest(
    @Autowired private val studyReviewRepository: MongoStudyReviewRepository,
    @Autowired private val systematicStudyRepository: MongoSystematicStudyRepository,
    @Autowired private val questionRepository: MongoQuestionRepository,
    @Autowired private val protocolRepository: MongoProtocolRepository,
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
    private lateinit var protocolDataFactory: ProtocolTestDataFactory

    @BeforeEach
    fun setUp() {
        faker = Faker()
        factory = TestDataFactory()
        systematicStudyDataFactory = SystematicStudyTestDataFactory()
        studyReviewDataFactory = StudyReviewTestDataFactory()
        protocolDataFactory = ProtocolTestDataFactory()
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

        studyReview = factory.createStudyReviewWithQuestions(
            systematicStudy.id,
            robQuestions,
            extractionQuestions
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

    private fun findAnswerUrl(systematicStudy: UUID = this.systematicStudy.id, questionId: UUID) =
        "/api/v1/systematic-study/$systematicStudy/report/find-answer/$questionId"
    private fun findCriteriaUrl(systematicStudy: UUID = this.systematicStudy.id, type: String) =
        "/api/v1/systematic-study/$systematicStudy/report/criteria/$type"

    @Nested
    @DisplayName("When searching answers of questions")
    inner class WhenSearchingAnswersOfQuestions {
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
                    get(findAnswerUrl(questionId = question.questionId))
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
                    get(findAnswerUrl(questionId = question.questionId))
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
                    get(findAnswerUrl(questionId = notAnsweredQuestions[0].questionId))
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
                    requestBuilder = get(findAnswerUrl(questionId = robQuestions[1].questionId))
                )
            }

            @Test
            fun `should not allow unauthorized users to find questions`() {
                testHelperService.testForUnauthorizedUser(
                    mockMvc = mockMvc,
                    requestBuilder = get(findAnswerUrl(questionId = robQuestions[1].questionId))
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
                    get(findAnswerUrl(questionId = question))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isNotFound)
            }
        }
    }

    @Nested
    @DisplayName("When searching for criteria")
    inner class WhenSearchingCriteria {

        @Nested
        @DisplayName("And finding them")
        inner class AndFindingThem {
            @Test
            fun `should return 200 and find the study included by the criteria`() {
                val protocol = protocolDataFactory.createProtocolDocument(
                    id = systematicStudy.id,
                )
                val criteria = protocol.selectionCriteria.first()
                val criteria2 = protocol.selectionCriteria.last()
                val studyReview = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 1111L,
                    criteria = setOf(criteria.description, criteria2.description),
                )

                val studyReview2 = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 2222L,
                    criteria = setOf(criteria.description, criteria2.description),
                )

                protocolRepository.save(protocol)
                studyReviewRepository.saveAll(listOf(studyReview, studyReview2))
                mockMvc.perform(
                    get(findCriteriaUrl(type = criteria.type))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isOk)
            }
        }

    }
}