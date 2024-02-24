package br.all.question.controller

import br.all.domain.model.question.QuestionId
import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.question.QuestionDocument
import br.all.question.utils.TestDataFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
class ExtractionQuestionControllerTest(
    @Autowired val repository: MongoQuestionRepository,
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
        researcherId = factory.researcherId
        questionId = factory.questionId
    }

    @AfterEach
    fun teardown() = repository.deleteAll()


    fun postUrl() = "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/protocol/extraction-question"

    fun getUrl(questionId: String = "") =
        "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/protocol/extraction-question/$questionId"


    @Nested
    @DisplayName("When successfully creating questions")
    inner class WhenSuccessfullyCreatingQuestions {
        @Test
        fun `should create textual question and return 201`() {
            val json = factory.validCreateTextualRequest()
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post(postUrl() + "/textual").contentType(MediaType.APPLICATION_JSON).content(json)
            )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should create picklist question and return 201`() {
            val json = factory.validCreatePickListRequest()
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post(postUrl() + "/pick-list").contentType(MediaType.APPLICATION_JSON).content(json)
            )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should create labeledscale question and return 201`() {
            val json = factory.validCreateLabeledScaleRequest()
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post(postUrl() + "/labeled-scale").contentType(MediaType.APPLICATION_JSON).content(json)
            )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should create numberscale question and return 201`() {
            val json = factory.validCreateNumberScaleRequest()
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post(postUrl() + "/number-scale").contentType(MediaType.APPLICATION_JSON).content(json)
            )
                .andDo(MockMvcResultHandlers.print())
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

            mockMvc.perform(get(getUrl(questionId.toString())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(question.systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should find picklist question and return 200`() {
            val question = factory.validCreatePickListQuestionDocument(questionId, systematicStudyId)

            repository.insert(question)

            mockMvc.perform(get(getUrl(questionId.toString())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(question.systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())

        }
        
        @Test
        fun `should find labeled scale question and return 200`() {
            val question = factory.validCreateLabeledScaleQuestionDocument(questionId, systematicStudyId)

            repository.insert(question)

            mockMvc.perform(get(getUrl(questionId.toString())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(question.systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should find numbered scale question and return 200`() {
            val question = factory.validCreateNumberedScaleQuestionDocument(questionId, systematicStudyId)

            repository.insert(question)

            mockMvc.perform(get(getUrl(questionId.toString())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(question.systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
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
                MockMvcRequestBuilders.post(postUrl() + "/textual")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should not create picklist question with invalid input and return 400`() {
            val json = factory.invalidCreatePickListRequest()
            mockMvc.perform(
                MockMvcRequestBuilders.post(postUrl() + "/pick-list")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should not create labeled scale question with invalid input and return 400`() {
            val json = factory.invalidCreateLabeledScaleRequest()
            mockMvc.perform(
                MockMvcRequestBuilders.post(postUrl() + "/labeled-scale")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should not create number scale question with invalid input and return 400`() {
            val json = factory.invalidCreateNumberScaleRequest()
            mockMvc.perform(
                MockMvcRequestBuilders.post(postUrl() + "/number-scale")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest)
        }
    }
}