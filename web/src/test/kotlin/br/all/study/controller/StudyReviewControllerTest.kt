package br.all.study.controller

import br.all.infrastructure.shared.toNullable
import br.all.infrastructure.study.MongoStudyReviewRepository
import br.all.infrastructure.study.StudyReviewId
import br.all.infrastructure.study.StudyReviewIdGeneratorService
import br.all.study.utils.TestDataFactory
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class StudyReviewControllerTest(
    @Autowired val repository: MongoStudyReviewRepository,
    @Autowired val idService: StudyReviewIdGeneratorService,
    @Autowired val mockMvc: MockMvc,
) {

    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyId: UUID
    private lateinit var researcherId: UUID

    fun postUrl() = "/api/v1/researcher/$researcherId/review/$systematicStudyId/study-review"
    fun findUrl(studyId: String = "") =
        "/api/v1/researcher/$researcherId/review/$systematicStudyId/study-review${studyId}"
    fun patchStatusStatus(attributeName: String, studyId: String) =
        "/api/v1/researcher/$researcherId/review/$systematicStudyId/study-review/${studyId}/${attributeName}"

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
        val json = factory.validPostRequest()
        mockMvc.perform(post(postUrl()).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
            .andExpect(jsonPath("$.studyReviewId").exists())
            .andExpect(jsonPath("$._links").exists())
    }

    @Test
    fun `should not create study with valid input and return 400`() {
        val json = factory.invalidPostRequest()
        mockMvc.perform(post(postUrl())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should find the study and return 200`() {
        val studyReview = factory.reviewDocument(systematicStudyId, idService.next())
        repository.insert(studyReview)

        val studyId = "/${studyReview.id.studyReviewId}"
        mockMvc.perform(get(findUrl(studyId)).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.systematicStudyId").value(studyReview.id.systematicStudyId.toString()))
            .andExpect(jsonPath("$._links").exists())
    }

    @Test
    fun `should return 404 if don't find the study review`() {
        mockMvc.perform(get(findUrl("/-1")).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should find all studies and return 200`() {
        repository.insert(factory.reviewDocument(systematicStudyId, idService.next(), "study"))
        repository.insert(factory.reviewDocument(systematicStudyId, idService.next(), "study"))
        repository.insert(factory.reviewDocument(UUID.randomUUID(), idService.next(), "study"))

        mockMvc.perform(get(findUrl()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
            .andExpect(jsonPath("$.size").value(2))
    }

    @Test
    fun `should return empty list and return 200 if no study is found`() {
        mockMvc.perform(get(findUrl()).contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
            .andExpect(jsonPath("$.size").value(0))
            .andExpect(jsonPath("$.studyReviews").isEmpty())
    }

    @Test
    fun `should update the study selection status and return 204`() {
        val studyId = idService.next()

        val statusToBeUpdated = "INCLUDED"
        val json = factory.validStatusUpdatePatchRequest(studyId, statusToBeUpdated)

        val studyReview = factory.reviewDocument(systematicStudyId, studyId)
        repository.insert(studyReview)

        mockMvc.perform(patch(patchStatusStatus("selection-status", studyId.toString()))
            .contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk)

        val studyReviewId = StudyReviewId(systematicStudyId, studyId)
        val updatedReview = repository.findById(studyReviewId).toNullable()
        val updatedStatus = updatedReview?.selectionStatus
        assertEquals(statusToBeUpdated, updatedStatus)
    }

    @Test
    fun `should not update the study selection if the status is invalid and return 400`() {
        val studyId = idService.next()

        val statusToBeUpdated = "KILLED"
        val json = factory.validStatusUpdatePatchRequest(studyId, statusToBeUpdated)

        val studyReview = factory.reviewDocument(systematicStudyId, studyId)
        repository.insert(studyReview)

        mockMvc.perform(patch(patchStatusStatus("selection-status", studyId.toString()))
            .contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest)

        val studyReviewId = StudyReviewId(systematicStudyId, studyId)
        val updatedReview = repository.findById(studyReviewId).toNullable()
        assertEquals(studyReview, updatedReview)
    }

    @Test
    fun `should update the study extraction status and return 204`() {
        val studyId = idService.next()

        val statusToBeUpdated = "EXCLUDED"
        val json = factory.validStatusUpdatePatchRequest(studyId, statusToBeUpdated)

        val studyReview = factory.reviewDocument(systematicStudyId, studyId)
        repository.insert(studyReview)

        val patchStatusStatus = patchStatusStatus("extraction-status", studyId.toString())
        mockMvc.perform(patch(patchStatusStatus)
            .contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk)

        val studyReviewId = StudyReviewId(systematicStudyId, studyId)
        val updatedReview = repository.findById(studyReviewId).toNullable()
        val updatedStatus = updatedReview?.extractionStatus
        assertEquals(statusToBeUpdated, updatedStatus)
    }

    @Test
    fun `should not update the study extraction if the status is invalid and return 400`() {
        val studyId = idService.next()

        val statusToBeUpdated = "DISPATCHED"
        val json = factory.validStatusUpdatePatchRequest(studyId, statusToBeUpdated)

        val studyReview = factory.reviewDocument(systematicStudyId, studyId)
        repository.insert(studyReview)

        val patchStatusStatus = patchStatusStatus("extraction-status", studyId.toString())
        mockMvc.perform(patch(patchStatusStatus)
            .contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest)

        val studyReviewId = StudyReviewId(systematicStudyId, studyId)
        val updatedReview = repository.findById(studyReviewId).toNullable()
        assertEquals(studyReview, updatedReview)
    }

    @Test
    fun `should update the study reading priority and return 204`() {
        val studyId = idService.next()

        val statusToBeUpdated = "HIGH"
        val json = factory.validStatusUpdatePatchRequest(studyId, statusToBeUpdated)

        val studyReview = factory.reviewDocument(systematicStudyId, studyId)
        repository.insert(studyReview)

        mockMvc.perform(patch(patchStatusStatus("reading-priority", studyId.toString()))
            .contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk)

        val studyReviewId = StudyReviewId(systematicStudyId, studyId)
        val updatedReview = repository.findById(studyReviewId).toNullable()
        val updatedStatus = updatedReview?.readingPriority
        assertEquals(statusToBeUpdated, updatedStatus)
    }

    @Test
    fun `should not update the study reading priority if the value is invalid and return 400`() {
        val studyId = idService.next()

        val statusToBeUpdated = "MEDIUM"
        val json = factory.validStatusUpdatePatchRequest(studyId, statusToBeUpdated)

        val studyReview = factory.reviewDocument(systematicStudyId, studyId)
        repository.insert(studyReview)

        mockMvc.perform(patch(patchStatusStatus("reading-priority", studyId.toString()))
            .contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest)

        val studyReviewId = StudyReviewId(systematicStudyId, studyId)
        val updatedReview = repository.findById(studyReviewId).toNullable()
        assertEquals(studyReview, updatedReview)
    }
}