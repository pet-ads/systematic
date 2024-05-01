package br.all.study.controller

import br.all.application.question.repository.QuestionRepository
import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.shared.toNullable
import br.all.infrastructure.study.MongoStudyReviewRepository
import br.all.infrastructure.study.StudyReviewId
import br.all.infrastructure.study.StudyReviewIdGeneratorService
import br.all.study.utils.TestDataFactory
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import br.all.review.shared.TestDataFactory as SystematicStudyTestDataFactory

@SpringBootTest
@AutoConfigureMockMvc
class StudyReviewControllerTest(
    @Autowired val repository: MongoStudyReviewRepository,
    @Autowired val systematicStudyRepository: MongoSystematicStudyRepository,
    @Autowired val idService: StudyReviewIdGeneratorService,
    @Autowired val mockMvc: MockMvc,
) {

    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyId: UUID
    private lateinit var researcherId: UUID

    fun postUrl() = "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/study-review"
    fun findUrl(studyId: String = "") =
        "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/study-review${studyId}"

    fun findBySourceUrl(searchSource: String = "") =
        "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/search-source/${searchSource}"

    fun updateStudyUrl(studyReviewId: Long) =
        "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/study-review/${studyReviewId}"

    fun updateStatusStatus(attributeName: String, studyId: String) =
        "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/study-review/${studyId}/${attributeName}"

    fun markAsDuplicated(studyIdToKeep: Long, studyIdDuplicate: Long) =
        "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/study-review/${studyIdToKeep}/duplicated/${studyIdDuplicate}"

    fun answerRiskOfBiasQuestion(studyReviewId: Long) =
        "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/study-review/${studyReviewId}/riskOfBias-answer"

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        idService.reset()

        factory = TestDataFactory()
        systematicStudyId = factory.systematicStudyId
        researcherId = factory.researcherId

        systematicStudyRepository.deleteAll()
        systematicStudyRepository.save(SystematicStudyTestDataFactory().createSystematicStudyDocument(
            id = systematicStudyId,
            owner = researcherId,
        ))
    }

    @AfterEach
    fun teardown() = repository.deleteAll()

    @Nested
    @DisplayName("When creating a study review")
    inner class CreateTests {
        @Test
        fun `should create study and return 201`() {
            val json = factory.validPostRequest()
            mockMvc.perform(post(postUrl()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.studyReviewId").exists())
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should not create study with invalid input and return 400`() {
            val json = factory.invalidPostRequest()
            mockMvc.perform(
                post(postUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest)
        }

    }

    @Nested
    @DisplayName("When updating study review")
    inner class UpdateTests {
        @Test
        fun `should update a study and return 200`(){
            val studyID = 20L
            val studyReview = factory.reviewDocument(systematicStudyId, studyID)
            val initialTitle = studyReview.title

            repository.insert(studyReview)

            val json = factory.validPutRequest(researcherId, systematicStudyId, studyID)
            mockMvc.perform(put(updateStudyUrl(studyID)).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk)

            val studyReviewId = StudyReviewId(systematicStudyId, studyID)
            val updatedReview = repository.findById(studyReviewId)
            val updatedTitle = updatedReview.get().title
            assertTrue(initialTitle != updatedTitle)
        }
        @Test
        fun `should not update upon invalid request and return 400`(){
            val studyId = 10L
            val studyReview = factory.reviewDocument(systematicStudyId, studyId)

            repository.insert(studyReview)

            val json = factory.invalidPutRequest()
            mockMvc.perform(put(updateStudyUrl(studyId)).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `should not update if study review does not exist and return 404`(){
            val studyId = 5L

            val json = factory.validPutRequest(researcherId, systematicStudyId, studyId)
            mockMvc.perform(put(updateStudyUrl(studyId)).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound)
        }

    }

    @Nested
    @DisplayName("When finding study review")
    inner class FindTests {
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
        fun `should find all studies by source and return 200`() {
            repository.insert(factory.reviewDocument(systematicStudyId, idService.next(), "study",
                sources = setOf("ACM")))
            repository.insert(factory.reviewDocument(systematicStudyId, idService.next(), "study"))
            repository.insert(factory.reviewDocument(UUID.randomUUID(), idService.next(), "study",
                sources = setOf("ACM")))

            mockMvc.perform(get(findBySourceUrl("ACM")).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.size").value(1))
        }
    }

    @Nested
    @DisplayName("When updating study review status")
    inner class UpdateStatusTests {
        @Test
        fun `should update the study selection status and return 200`() {
            val studyId = idService.next()

            val statusToBeUpdated = "INCLUDED"
            val json = factory.validStatusUpdatePatchRequest(studyId, statusToBeUpdated)

            val studyReview = factory.reviewDocument(systematicStudyId, studyId)
            repository.insert(studyReview)

            mockMvc.perform(
                patch(updateStatusStatus("selection-status", studyId.toString()))
                    .contentType(MediaType.APPLICATION_JSON).content(json)
            ).andExpect(status().isOk)

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

            mockMvc.perform(
                patch(updateStatusStatus("selection-status", studyId.toString()))
                    .contentType(MediaType.APPLICATION_JSON).content(json)
            ).andExpect(status().isBadRequest)

            val studyReviewId = StudyReviewId(systematicStudyId, studyId)
            val updatedReview = repository.findById(studyReviewId).toNullable()
            assertEquals(studyReview, updatedReview)
        }

        @Test
        fun `should update the study extraction status and return 200`() {
            val studyId = idService.next()

            val statusToBeUpdated = "EXCLUDED"
            val json = factory.validStatusUpdatePatchRequest(studyId, statusToBeUpdated)

            val studyReview = factory.reviewDocument(systematicStudyId, studyId)
            repository.insert(studyReview)

            val patchStatusStatus = updateStatusStatus("extraction-status", studyId.toString())
            mockMvc.perform(patch(patchStatusStatus).contentType(MediaType.APPLICATION_JSON).content(json))
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

            val patchStatusStatus = updateStatusStatus("extraction-status", studyId.toString())
            mockMvc.perform(patch(patchStatusStatus).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest)

            val studyReviewId = StudyReviewId(systematicStudyId, studyId)
            val updatedReview = repository.findById(studyReviewId).toNullable()
            assertEquals(studyReview, updatedReview)
        }

        @Test
        fun `should update the study reading priority and return 200`() {
            val studyId = idService.next()

            val statusToBeUpdated = "HIGH"
            val json = factory.validStatusUpdatePatchRequest(studyId, statusToBeUpdated)

            val studyReview = factory.reviewDocument(systematicStudyId, studyId)
            repository.insert(studyReview)

            mockMvc.perform(
                patch(updateStatusStatus("reading-priority", studyId.toString()))
                    .contentType(MediaType.APPLICATION_JSON).content(json)
            ).andExpect(status().isOk)

            val studyReviewId = StudyReviewId(systematicStudyId, studyId)
            val updatedReview = repository.findById(studyReviewId).toNullable()
            val updatedStatus = updatedReview?.readingPriority
            assertEquals(statusToBeUpdated, updatedStatus)
        }

        @Test
        fun `should not update the study reading priority if the status is invalid and return 400`() {
            val studyId = idService.next()

            val statusToBeUpdated = "NONE"
            val json = factory.validStatusUpdatePatchRequest(studyId, statusToBeUpdated)

            val studyReview = factory.reviewDocument(systematicStudyId, studyId)
            repository.insert(studyReview)

            val patchStatusStatus = updateStatusStatus("reading-priority", studyId.toString())
            mockMvc.perform(patch(patchStatusStatus).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest)

            val studyReviewId = StudyReviewId(systematicStudyId, studyId)
            val updatedReview = repository.findById(studyReviewId).toNullable()
            assertEquals(studyReview, updatedReview)
        }
    }

    @Nested
    @DisplayName("When answering questions in a review")
    inner class AnswerQuestionsTests(
        @Autowired val questionRepository: MongoQuestionRepository
    ){

        //TODO likely to be a json parsing error
        @Disabled
        @Test
        fun `should assign answer to question and return 200`(){
            val studyId = idService.next()
            val questionId = UUID.randomUUID()

            val studyReview = factory.reviewDocument(systematicStudyId, studyId)
            repository.insert(studyReview)

            val question = factory.generateQuestionTextualDto(questionId, systematicStudyId = systematicStudyId)
            questionRepository.insert(question)

            val json = factory.validAnswerRiskOfBiasPatchRequest(studyId, questionId, "TEXTUAL", "TEST")
            mockMvc.perform(patch(answerRiskOfBiasQuestion(studyId)).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk)

            val studyReviewId = StudyReviewId(systematicStudyId, studyId)
            val updatedReview = repository.findById(studyReviewId)
            assertEquals(updatedReview.get().formAnswers[questionId], "TEST")
        }

    }

    @Nested
    @DisplayName("When marking a study review as duplicated")
    inner class MarkingAsDuplicatedTests {
        @Test
        fun `should mark as duplicated and return 200`() {
            val studyToUpdateId = idService.next()
            val studyToDuplicateId = idService.next()

            val studyReviewToUpdate = factory.reviewDocument(systematicStudyId, studyToUpdateId)
            repository.insert(studyReviewToUpdate)
            val studyReviewToDuplicate = factory.reviewDocument(systematicStudyId, studyToDuplicateId)
            repository.insert(studyReviewToDuplicate)

            mockMvc.perform(patch(markAsDuplicated(studyToUpdateId, studyToDuplicateId)))
                .andExpect(status().isOk)

            val updatedStudyId = StudyReviewId(systematicStudyId, studyToUpdateId)
            val updatedStudy = repository.findById(updatedStudyId).toNullable()

            val repeatedStudyId = StudyReviewId(systematicStudyId, studyToDuplicateId)
            val repeatedStudy = repository.findById(repeatedStudyId).toNullable()

            val sources = repeatedStudy?.searchSources?.toMutableSet() ?: mutableSetOf()
            updatedStudy?.searchSources?.forEach { sources.add(it) }

            assertAll(
                { assertEquals(repeatedStudy?.selectionStatus, "DUPLICATED") },
                { assertEquals(updatedStudy?.searchSources?.toMutableSet(), sources) },
            )
        }

        @Test
        fun `should return 404 if don't find the study to be marked as duplicated`() {
            val studyToUpdateId = idService.next()
            val studyToDuplicateId = idService.next()

            val studyReviewToDuplicate = factory.reviewDocument(systematicStudyId, studyToDuplicateId)
            repository.insert(studyReviewToDuplicate)

            mockMvc.perform(patch(markAsDuplicated(studyToUpdateId, studyToDuplicateId))).andExpect(status().isNotFound)
        }

        @Test
        fun `should return 404 if don't find the study to be updated`() {
            val studyToUpdateId = idService.next()
            val studyToDuplicateId = idService.next()

            val studyReviewToUpdate = factory.reviewDocument(systematicStudyId, studyToUpdateId)
            repository.insert(studyReviewToUpdate)

            mockMvc.perform(patch(markAsDuplicated(studyToUpdateId, studyToDuplicateId))).andExpect(status().isNotFound)
        }
    }
}