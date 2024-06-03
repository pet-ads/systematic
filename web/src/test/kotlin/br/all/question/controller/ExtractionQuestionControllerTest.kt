package br.all.question.controller

import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.question.utils.TestDataFactory
import br.all.security.service.ApplicationUser
import br.all.shared.TestHelperService
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
class ExtractionQuestionControllerTest(
    @Autowired val repository: MongoQuestionRepository,
    @Autowired val systematicStudyRepository: MongoSystematicStudyRepository,
    @Autowired val mockMvc: MockMvc,
    @Autowired private val testHelperService: TestHelperService,
    ) {
    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyId: UUID
    private lateinit var researcherId: UUID
    private lateinit var questionId: UUID
    private lateinit var user: ApplicationUser

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        factory = TestDataFactory()
        systematicStudyId = factory.systematicStudyId
        researcherId = factory.researcherId
        questionId = factory.questionId
        user = testHelperService.createApplicationUser()
        systematicStudyRepository.deleteAll()
        systematicStudyRepository.save(
            br.all.review.shared.TestDataFactory().createSystematicStudyDocument(
            id = systematicStudyId,
            owner = user.id,
        ))
    }

    @AfterEach
    fun teardown() {
        repository.deleteAll()
        testHelperService.deleteApplicationUser(user.id)
    }

    fun postUrl() = "/api/v1/systematic-study/$systematicStudyId/protocol/extraction-question"

    fun getUrl(questionId: String = "") =
        "/api/v1/systematic-study/$systematicStudyId/protocol/extraction-question${questionId}"


    @Nested
    @DisplayName("When successfully creating questions")
    inner class WhenSuccessfullyCreatingQuestions {
        @Test
        fun `should create textual question and return 201`() {
            val json = factory.validCreateTextualRequest()
            mockMvc.perform(
                post(postUrl() + "/textual")
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(json)
            )
                .andDo(print())
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should create picklist question and return 201`() {
            val json = factory.validCreatePickListRequest()
            mockMvc.perform(
                post(postUrl() + "/pick-list").contentType(MediaType.APPLICATION_JSON).content(json)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
            )
                .andDo(print())
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should create labeledscale question and return 201`() {
            val json = factory.validCreateLabeledScaleRequest()
            mockMvc.perform(
                post(postUrl() + "/labeled-scale").contentType(MediaType.APPLICATION_JSON).content(json)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
            )
                .andDo(print())
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should create numberscale question and return 201`() {
            val json = factory.validCreateNumberScaleRequest()
            mockMvc.perform(
                post(postUrl() + "/number-scale").contentType(MediaType.APPLICATION_JSON).content(json)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
            )
                .andDo(print())
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }
    }

    @Nested
    @DisplayName("when successfully finding questions")
    inner class WhenSuccessfullyFindingQuestions {
        @Test
        fun `should find textual question and return 200`() {
            val question = factory.validCreateTextualQuestionDocument(questionId, systematicStudyId)

            repository.insert(question)

            val questionIdUrl = "/${questionId}"
            mockMvc.perform(get(getUrl(questionIdUrl)).contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(question.systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should find picklist question and return 200`() {
            val question = factory.validCreatePickListQuestionDocument(questionId, systematicStudyId)

            repository.insert(question)

            val questionIdUrl = "/${questionId}"
            mockMvc.perform(get(getUrl(questionIdUrl)).contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
            )

                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(question.systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())

        }

        @Test
        fun `should find labeled scale question and return 200`() {
            val question = factory.validCreateLabeledScaleQuestionDocument(questionId, systematicStudyId)

            repository.insert(question)

            val questionIdUrl = "/${questionId}"
            mockMvc.perform(get(getUrl(questionIdUrl)).contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(question.systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should find numbered scale question and return 200`() {
            val question = factory.validCreateNumberedScaleQuestionDocument(questionId, systematicStudyId)

            repository.insert(question)

            val questionIdUrl = "/${questionId}"
            mockMvc.perform(get(getUrl(questionIdUrl)).contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(question.systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should find all questions and return 200`() {
            val textualQuestion = factory.validCreateTextualQuestionDocument(UUID.randomUUID(), systematicStudyId)
            val pickListQuestion = factory.validCreatePickListQuestionDocument(UUID.randomUUID(), systematicStudyId)

            repository.insert(textualQuestion)
            repository.insert(pickListQuestion)

            mockMvc.perform(get(getUrl()).contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.size").value(2))
        }

        @Test
        fun `should return an empty list and return 200 if no study is found`() {
            mockMvc.perform(get(getUrl()).contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.size").value(0))
                .andExpect(jsonPath("$.questions").isEmpty())
        }


    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("when not able to create question successfully")
    inner class WhenNotAbleToCreateQuestionSuccessfully {
        @Test
        fun `should not create textual question with invalid input and return 400`() {
            val json = factory.invalidCreateTextualRequest()
            mockMvc.perform(
                post(postUrl() + "/textual")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should not create picklist question with invalid input and return 400`() {
            val json = factory.invalidCreatePickListRequest()
            mockMvc.perform(
                post(postUrl() + "/pick-list")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should not create labeled scale question with invalid input and return 400`() {
            val json = factory.invalidCreateLabeledScaleRequest()
            mockMvc.perform(
                post(postUrl() + "/labeled-scale")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should not create number scale question with invalid input and return 400`() {
            val json = factory.invalidCreateNumberScaleRequest()
            mockMvc.perform(
                post(postUrl() + "/number-scale")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should not create question when user is not authenticated`() {
            testHelperService.testForUnauthenticatedUser(mockMvc,
                post(postUrl() + "/textual")
                    .content(factory.validCreateTextualRequest()),
            )
        }

        @Test
        fun `should not create question when user is unauthorized`() {
            testHelperService.testForUnauthorizedUser(mockMvc,
                post(postUrl() + "/textual")
                    .content(factory.validCreateTextualRequest())
            )
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("when not able to find question successfully")
    inner class WhenNotAbleToFindQuestionSuccessfully {
        @Test
        fun `should return 404 if don't find the question`() {
            mockMvc.perform(get(getUrl(UUID.randomUUID().toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should not find question when user is unauthenticated`(){
            testHelperService.testForUnauthenticatedUser(mockMvc, get(getUrl()),
            )
        }

        @Test
        fun `should not find question when user is unauthorized`(){
            testHelperService.testForUnauthorizedUser(mockMvc, get(getUrl())
            )
        }

    }
}