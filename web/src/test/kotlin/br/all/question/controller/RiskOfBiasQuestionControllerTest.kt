package br.all.question.controller

import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.question.utils.TestDataFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
class RiskOfBiasQuestionControllerTest(
    @Autowired val repository: MongoQuestionRepository,
    @Autowired val systematicStudyRepository: MongoSystematicStudyRepository,
    @Autowired val mockMvc: MockMvc,
) {
    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyId: UUID
    private lateinit var researcherId: UUID
    private lateinit var questionId: UUID

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        factory = TestDataFactory()
        systematicStudyId = factory.systematicStudyId
        questionId = factory.questionId
        researcherId = factory.researcherId
        systematicStudyRepository.deleteAll()
        systematicStudyRepository.save(
            br.all.review.shared.TestDataFactory().createSystematicStudyDocument(
                id = systematicStudyId,
                owner = researcherId,
            )
        )
    }

    @AfterEach
    fun teardown() = repository.deleteAll()


    fun postUrl(
        researcherId: UUID = factory.researcherId,
        systematicStudyId: UUID = factory.systematicStudyId
    ) = "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/protocol/rob-question"

    fun getUrl(
        questionId: String = "",
        researcherId: UUID = factory.researcherId,
        systematicStudyId: UUID = factory.systematicStudyId
    ) =
        "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/protocol/rob-question${questionId}"


    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating questions")
    inner class WhenSuccessfullyCreatingQuestions {
        @Test
        fun `should create textual question and return 201`() {
            val json = factory.validCreateTextualRequest()
            mockMvc.perform(
                post(postUrl() + "/textual").contentType(MediaType.APPLICATION_JSON).content(json)
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
            )
                .andDo(print())
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should create labeled scale question and return 201`() {
            val json = factory.validCreateLabeledScaleRequest()
            mockMvc.perform(
                post(postUrl() + "/labeled-scale").contentType(MediaType.APPLICATION_JSON).content(json)
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
            mockMvc.perform(get(getUrl(questionIdUrl)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(question.systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should find picklist question and return 200`() {
            val question = factory.validCreatePickListQuestionDocument(questionId, systematicStudyId)

            repository.insert(question)

            val questionIdUrl = "/${questionId}"
            mockMvc.perform(get(getUrl(questionIdUrl)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(question.systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())

        }

        @Test
        fun `should find labeled scale question and return 200`() {
            val question = factory.validCreateLabeledScaleQuestionDocument(questionId, systematicStudyId)

            repository.insert(question)

            val questionIdUrl = "/${questionId}"
            mockMvc.perform(get(getUrl(questionIdUrl)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(question.systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should find numbered scale question and return 200`() {
            val question = factory.validCreateNumberedScaleQuestionDocument(questionId, systematicStudyId)

            repository.insert(question)

            val questionIdUrl = "/${questionId}"
            mockMvc.perform(get(getUrl(questionIdUrl)).contentType(MediaType.APPLICATION_JSON))
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

            mockMvc.perform(get(getUrl()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.size").value(2))
        }

        @Test
        fun `should return an empty list and return 200 if no study is found`() {
            mockMvc.perform(get(getUrl()).contentType(MediaType.APPLICATION_JSON))
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
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should not create picklist question with invalid input and return 400`() {
            val json = factory.invalidCreatePickListRequest()
            mockMvc.perform(
                post(postUrl() + "/pick-list")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should not create labeled scale question with invalid input and return 400`() {
            val json = factory.invalidCreateLabeledScaleRequest()
            mockMvc.perform(
                post(postUrl() + "/labeled-scale")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should not create number scale question with invalid input and return 400`() {
            val json = factory.invalidCreateNumberScaleRequest()
            mockMvc.perform(
                post(postUrl() + "/number-scale")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should a researcher who is not a collaborator be unauthorized and return 403`() {
            val json = factory.validCreateTextualRequest()
            val notAllowed = UUID.randomUUID()
            mockMvc.perform(
                post(postUrl(notAllowed) + "/textual")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `should return 404 when trying to create a question with a nonexistent systematicStudy`() {
            val json = factory.validCreateTextualRequest()
            val notAllowed = UUID.randomUUID()
            mockMvc.perform(
                post(postUrl(systematicStudyId = notAllowed) + "/textual")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isNotFound)
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("when not able to find question successfully")
    inner class WhenNotAbleToFindQuestionSuccessfully {
        @Test
        fun `should return 404 if don't find the question`() {
            mockMvc.perform(
                get(getUrl(UUID.randomUUID().toString())).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should a researcher who is not a collaborator be unauthorized and return 403`() {
            val question = factory.validCreateTextualQuestionDocument(questionId, systematicStudyId)
            repository.insert(question)
            val notAllowed = UUID.randomUUID()
            val questionIdUrl = "/${questionId}"

            mockMvc.perform(
                get(
                    getUrl(
                        questionIdUrl,
                        researcherId = notAllowed
                    )
                ).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isForbidden)
        }
    }
}