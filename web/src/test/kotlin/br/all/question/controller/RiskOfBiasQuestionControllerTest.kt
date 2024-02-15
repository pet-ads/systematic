package br.all.question.controller

import br.all.infrastructure.question.MongoQuestionRepository
import br.all.question.utils.TestDataFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
class RiskOfBiasQuestionControllerTest(
    @Autowired val repository: MongoQuestionRepository,
    @Autowired val mockMvc: MockMvc,
) {
    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyId: UUID
    private lateinit var researcherId: UUID

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        factory = TestDataFactory()
        systematicStudyId = factory.systematicStudyId
        researcherId = factory.researcherId
    }

    @AfterEach
    fun teardown() = repository.deleteAll()


    fun postUrl() = "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/protocol/rob-question"

    fun getUrl(questionId: String = "") =
        "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/protocol/rob-question/$questionId"


    @Nested
    @Tag("ValidClasses")
    @DisplayName("When creating questions")
    inner class WhenCreatingQuestions {
        @Test
        fun `should create textual question and return 201`() {
            val json = factory.validCreateTextualRequest()
            mockMvc.perform(MockMvcRequestBuilders
                .post(postUrl() + "/textual").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links").exists())
        }

        @Test
        fun `should create picklist question and return 201`() {
            val json = factory.validCreatePickListRequest()
            mockMvc.perform(MockMvcRequestBuilders
                .post(postUrl() + "/pick-list").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links").exists())
        }

        @Test
        fun `should create labeledscale question and return 201`() {
            val json = factory.validCreateLabeledScaleRequest()
            mockMvc.perform(MockMvcRequestBuilders
                .post(postUrl() + "/labeled-scale").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links").exists())
        }

        @Test
        fun `should create numberscale question and return 201`() {
            val json = factory.validCreateNumberScaleRequest()
            mockMvc.perform(MockMvcRequestBuilders
                .post(postUrl() + "/number-scale").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links").exists())
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("when not able to create question successfully")
    inner class WhenNotAbleToCreateQuestionSuccessfully{
        @Test
        fun `should not create textual question with invalid input and return 400`() {
            val json = factory.invalidCreateTextualRequest()
            mockMvc.perform(
                post(postUrl() + "/textual")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest)
        }
    }
}