package br.all.study.controller

import br.all.infrastructure.study.MongoStudyReviewRepository
import br.all.infrastructure.study.StudyReviewIdGeneratorService
import br.all.study.utils.TestDataFactory
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class StudyReviewControllerTest(
    @Autowired val repository: MongoStudyReviewRepository,
    @Autowired val idService: StudyReviewIdGeneratorService,
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) {

    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyId: UUID
    private lateinit var researcherId: UUID

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        idService.reset()
        factory = TestDataFactory()
        systematicStudyId = factory.systematicStudyId
        researcherId = factory.researcherId
    }

    @AfterEach
    fun teardown() = repository.deleteAll()

    @Test
    fun `should create study and return 201`() {
        val requestModel = factory.validPostRequest
        val json = objectMapper.writeValueAsString(requestModel)

        mockMvc.perform(
            post("/api/v1/researcher/$researcherId/review/$systematicStudyId/study-review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.reviewId").value(systematicStudyId.toString()))
            .andExpect(jsonPath("$._links").exists())
            .andExpect(jsonPath("$.studyId").exists())
    }

    @Test
    fun `should find the study and return 200`() {
        val studyReview = factory.reviewDocument(systematicStudyId, idService.next(),"study")
        repository.insert(studyReview)

        val url = "/api/v1/researcher/$researcherId/review/$systematicStudyId/study-review/${studyReview.id.studyId}"
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.studyType").value(studyReview.type))
            .andExpect(jsonPath("$.reviewId").value(studyReview.id.reviewId.toString()))
            .andExpect(jsonPath("$.title").value(studyReview.title))
            .andExpect(jsonPath("$._links").exists())

    }

    @Test
    fun `should find all studies and return 200`() {
        val url = "/api/v1/researcher/$researcherId/review/$systematicStudyId/study-review"

        repository.insert(factory.reviewDocument(systematicStudyId, idService.next(), "study"))
        repository.insert(factory.reviewDocument(systematicStudyId, idService.next(), "study"))
        repository.insert(factory.reviewDocument(UUID.randomUUID(), idService.next(), "study"))

        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.reviewId").value(systematicStudyId.toString()))
            .andExpect(jsonPath("$.size").value(2))
    }

}