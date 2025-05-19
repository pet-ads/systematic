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

    private val baseReportUrl: String
        get() = "/api/v1/systematic-study/${systematicStudy.id}/report"

    private fun findAnswerUrl(questionId: UUID) =
        "$baseReportUrl/find-answer/$questionId"

    private fun findCriteriaUrl(type: String) =
        "$baseReportUrl/criteria/$type"

    private fun findSourcesUrl(source: String) =
        "$baseReportUrl/source/$source"

    private fun formatProtocolUrl(type: String) =
        "$baseReportUrl/exportable-protocol/$type?downloadable=false"

    private fun authorNetworkUrl() =
        "$baseReportUrl/author-network"

    private fun findStudiesByStageUrl(stage: String) =
        "$baseReportUrl/studies/$stage"

    private fun studiesFunnelUrl() =
        "$baseReportUrl/studies-funnel"

    private fun findKeywordsUrl(filter: String?) =
        if (filter.isNullOrBlank())
            "$baseReportUrl/keywords"
        else
            "$baseReportUrl/keywords?filter=$filter"

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

    @Nested
    @DisplayName("When searching studies by source")
    inner class WhenSearchingStudyBySource {
        @Nested
        @DisplayName("And finding them")
        inner class AndFindingThem {
            @Test
            fun `should return 200 and find the studies by source`() {
                val protocol = protocolDataFactory.createProtocolDocument(
                    id = systematicStudy.id,
                    informationSources = setOf("Scopus")
                )
                val studyReviews = (1111L..1115L).map {
                        id -> studyReviewDataFactory.reviewDocument(
                        systematicStudyId = systematicStudy.id,
                        studyReviewId = id,
                        selectionStatus = faker.random.randomValue(listOf("INCLUDED", "EXCLUDED", "DUPLICATED")),
                        sources = setOf("Scopus"),
                    )
                }

                protocolRepository.save(protocol)
                studyReviewRepository.saveAll(studyReviews)

                val result = mockMvc.perform(
                    get(findSourcesUrl(source = "Scopus"))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isOk)
                    .andReturn()

                println(result.response.contentAsString)
            }

        }

    }

    @Nested
    @DisplayName("When exporting formatted protocols")
    inner class WhenExportingProtocols {
        @Nested
        @DisplayName("And having success")
        inner class WhenHavingSuccess {
            @Test
            fun `should return 200 and return formatted protocol`() {
                val protocolDocument = protocolDataFactory.createProtocolDocument(
                    id = systematicStudy.id,
                )
                protocolRepository.save(protocolDocument)

                val r = mockMvc.perform(
                    get(formatProtocolUrl(type = "csv"))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isOk)
                    .andReturn()

                println(r.response.contentAsString)
            }
        }
    }

    @Nested
    @DisplayName("When generating author network")
    inner class WhenGeneratingNetwork {
        @Nested
        @DisplayName("And having success")
        inner class AndHavingSuccess {
            @Test
            fun `should return 200 and return author network`() {
                val authors1: List<String> = List(4) { faker.name.firstName() }
                val authors2: List<String> = List(4) { faker.name.firstName() }

                val study1 = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 11111,
                    authors = authors1.toString()
                )
                val study2 = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 22222,
                    authors = authors2.toString()
                )
                val study3 = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 33333,
                    authors = authors2.toString()
                )

                studyReviewRepository.saveAll(listOf(study1, study2, study3))

                val r = mockMvc.perform(
                    get(authorNetworkUrl())
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isOk)
                    .andReturn()

                println(r.response.contentAsString)
            }
        }
    }

    @Nested
    @DisplayName("When finding studies by stage")
    inner class WhenFindingStudiesByStage {
        @Nested
        @DisplayName("And having success")
        inner class AndHavingSuccess {
            @Test
            fun `should return 200 and find studies by selection stage`() {
                val includedStudy = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 11111,
                    selectionStatus = "INCLUDED"
                )
                val includedStudy2 = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 21111,
                    selectionStatus = "INCLUDED"
                )
                val excludedStudy = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 22222,
                    selectionStatus = "EXCLUDED"
                )
                val unclassifiedStudy = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 33333,
                    selectionStatus = "UNCLASSIFIED"
                )
                val duplicatedStudy = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 44444,
                    selectionStatus = "DUPLICATED"
                )

                studyReviewRepository.saveAll(listOf(includedStudy, includedStudy2, excludedStudy, unclassifiedStudy, duplicatedStudy))

                val result = mockMvc.perform(
                    get(findStudiesByStageUrl(stage = "selection"))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isOk)
                    .andReturn()

                println(result.response.contentAsString)
            }

            @Test
            fun `should return 200 and find studies by extraction stage`() {
                val includedStudy = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 55555,
                    extractionStatus = "INCLUDED"
                )
                val excludedStudy = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 66666,
                    extractionStatus = "EXCLUDED"
                )
                val unclassifiedStudy = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 77777,
                    extractionStatus = "UNCLASSIFIED"
                )
                val duplicatedStudy = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 88888,
                    extractionStatus = "DUPLICATED"
                )

                studyReviewRepository.saveAll(listOf(includedStudy, excludedStudy, unclassifiedStudy, duplicatedStudy))

                val result = mockMvc.perform(
                    get(findStudiesByStageUrl(stage = "extraction"))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isOk)
                    .andReturn()

                println(result.response.contentAsString)
            }
        }

        @Nested
        @DisplayName("And having failure")
        inner class AndHavingFailure {
            @Test
            fun `should return 400 when stage is invalid`() {
                mockMvc.perform(
                    get(findStudiesByStageUrl(stage = "invalid_stage"))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isBadRequest)
            }

            @Test
            fun `should not allow unauthenticated user to find studies by stage`() {
                testHelperService.testForUnauthenticatedUser(
                    mockMvc = mockMvc,
                    requestBuilder = get(findStudiesByStageUrl(stage = "selection"))
                )
            }

            @Test
            fun `should not allow unauthorized users to find studies by stage`() {
                testHelperService.testForUnauthorizedUser(
                    mockMvc = mockMvc,
                    requestBuilder = get(findStudiesByStageUrl(stage = "selection"))
                )
            }
        }
    }

    @Nested
    @DisplayName("When getting studies funnel")
    inner class WhenGettingStudiesFunnel {
        @Nested
        @DisplayName("And having success")
        inner class AndHavingSuccess {
            @Test
            fun `should return 200 and get studies funnel data`() {
                val includedStudy = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 11111,
                    selectionStatus = "INCLUDED",
                    extractionStatus = "INCLUDED",
                    sources = setOf("Scopus", "IEEE")
                )
                val excludedInSelectionStudy = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 22222,
                    selectionStatus = "EXCLUDED",
                    criteria = setOf("Criterion 1", "Criterion 2"),
                    sources = setOf("ACM")
                )
                val excludedInExtractionStudy = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 33333,
                    selectionStatus = "INCLUDED",
                    extractionStatus = "EXCLUDED",
                    criteria = setOf("Criterion 3"),
                    sources = setOf("Scopus")
                )
                val duplicatedStudy = studyReviewDataFactory.reviewDocument(
                    systematicStudyId = systematicStudy.id,
                    studyReviewId = 44444,
                    selectionStatus = "DUPLICATED",
                    sources = setOf("IEEE")
                )

                studyReviewRepository.saveAll(listOf(includedStudy, excludedInSelectionStudy, excludedInExtractionStudy, duplicatedStudy))

                val result = mockMvc.perform(
                    get(studiesFunnelUrl())
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                )
                    .andExpect(status().isOk)
                    .andReturn()

                println(result.response.contentAsString)
            }
        }

        @Nested
        @DisplayName("And having failure")
        inner class AndHavingFailure {
            @Test
            fun `should not allow unauthenticated user to get studies funnel`() {
                testHelperService.testForUnauthenticatedUser(
                    mockMvc = mockMvc,
                    requestBuilder = get(studiesFunnelUrl())
                )
            }

            @Test
            fun `should not allow unauthorized users to get studies funnel`() {
                testHelperService.testForUnauthorizedUser(
                    mockMvc = mockMvc,
                    requestBuilder = get(studiesFunnelUrl())
                )
            }
        }
    }
}
