package br.all.study.controller

import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.shared.toNullable
import br.all.infrastructure.study.MongoStudyReviewRepository
import br.all.infrastructure.study.StudyReviewId
import br.all.infrastructure.study.StudyReviewIdGeneratorService
import br.all.security.service.ApplicationUser
import br.all.shared.TestHelperService
import br.all.study.utils.TestDataFactory
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import br.all.review.shared.TestDataFactory as SystematicStudyTestDataFactory

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
@Tag("ControllerTest")
class StudyReviewControllerTest(
    @Autowired val repository: MongoStudyReviewRepository,
    @Autowired val systematicStudyRepository: MongoSystematicStudyRepository,
    @Autowired private val testHelperService: TestHelperService,
    @Autowired val idService: StudyReviewIdGeneratorService,
    @Autowired val mockMvc: MockMvc,
) {

    private lateinit var factory: TestDataFactory
    private lateinit var user: ApplicationUser

    private lateinit var systematicStudyId: UUID
    private lateinit var searchSessionId: UUID

    fun postUrl() = "/api/v1/systematic-study/$systematicStudyId/study-review"
    fun findUrl(studyId: String = "") =
        "/api/v1/systematic-study/$systematicStudyId/study-review${studyId}"

    fun findBySourceUrl(searchSource: String = "") =
        "/api/v1/systematic-study/$systematicStudyId/search-source/${searchSource}"

    fun findBySessionUrl(sessionId: String = "") =
        "/api/v1/systematic-study/$systematicStudyId/find-by-search-session/${sessionId}"

    fun findByAuthorUrl(author: String = "") =
        "/api/v1/systematic-study/$systematicStudyId/study-review/author/${author}"

    fun updateStudyUrl(studyReviewId: Long) =
        "/api/v1/systematic-study/$systematicStudyId/study-review/${studyReviewId}"

    fun updateStatusStatus(attributeName: String) =
        "/api/v1/systematic-study/$systematicStudyId/study-review/${attributeName}"

    fun markAsDuplicatedUrl(studyToUpdateId: Long) =
        "/api/v1/systematic-study/$systematicStudyId/study-review/$studyToUpdateId/duplicated"

    fun answerRiskOfBiasQuestion(studyReviewId: Long) =
        "/api/v1/systematic-study/$systematicStudyId/study-review/${studyReviewId}/riskOfBias-answer"

    fun answerExtractionQuestion(studyReviewId: Long) =
        "/api/v1/systematic-study/$systematicStudyId/study-review/${studyReviewId}/extraction-answer"

    fun batchAnswerRiskOfBiasQuestion(studyReviewId: Long) =
        "/api/v1/systematic-study/$systematicStudyId/study-review/${studyReviewId}/batch-riskOfBias-answers"

    fun batchAnswerExtractionQuestion(studyReviewId: Long) =
        "/api/v1/systematic-study/$systematicStudyId/study-review/${studyReviewId}/batch-extraction-answers"

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        idService.reset()

        factory = TestDataFactory()
        systematicStudyId = factory.systematicStudyId
        searchSessionId = factory.searchSessionId

        user = testHelperService.createApplicationUser()

        systematicStudyRepository.deleteAll()
        systematicStudyRepository.save(
            SystematicStudyTestDataFactory().createSystematicStudyDocument(
                id = systematicStudyId,
                owner = user.id,
            )
        )
    }

    @AfterEach
    fun teardown() {
        repository.deleteAll()
        testHelperService.deleteApplicationUser(user.id)
    }

    @Nested
    @DisplayName("When creating a study review")
    inner class CreateTests {
        @Test
        fun `should create study and return 201`() {
            val json = factory.validPostRequest()
            mockMvc.perform(
                post(postUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(json)
            )
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
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should not create if user is unauthorized`(){
            testHelperService.testForUnauthorizedUser(
                mockMvc,
                post(postUrl()).content(factory.validPostRequest())
            )
        }

        @Test
        fun `should not create if user is unauthenticated`() {
            testHelperService.testForUnauthenticatedUser(mockMvc, post(postUrl()),
            )
        }

    }

    @Nested
    @DisplayName("When updating study review")
    inner class UpdateTests {
        @Test
        fun `should update a study and return 200`() {
            val studyID = 20L
            val studyReview = factory.reviewDocument(systematicStudyId, studyID)
            val initialTitle = studyReview.title

            repository.insert(studyReview)

            val json = factory.validPutRequest(systematicStudyId, studyID)
            mockMvc.perform(put(updateStudyUrl(studyID))
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON).content(json)
            )
                .andExpect(status().isOk)

            val studyReviewId = StudyReviewId(systematicStudyId, studyID)
            val updatedReview = repository.findById(studyReviewId)
            val updatedTitle = updatedReview.get().title
            assertTrue(initialTitle != updatedTitle)
        }

        @Test
        fun `should not update upon invalid request and return 400`() {
            val studyId = 10L
            val studyReview = factory.reviewDocument(systematicStudyId, studyId)

            repository.insert(studyReview)

            val json = factory.invalidPutRequest()
            mockMvc.perform(put(updateStudyUrl(studyId))
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON).content(json)
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `should not update if study review does not exist and return 404`() {
            val studyId = 5L

            val json = factory.validPutRequest(systematicStudyId, studyId)
            mockMvc.perform(put(updateStudyUrl(studyId))
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON).content(json)
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should not update if user is unauthorized`(){
            val studyId = 5L

            testHelperService.testForUnauthorizedUser(mockMvc,
                put(updateStudyUrl(studyId)).content(factory.validPutRequest(systematicStudyId, studyId))
            )
        }

        @Test
        fun `should not update if user is unauthenticated`(){
            val studyId = 5L

            testHelperService.testForUnauthenticatedUser(mockMvc, put(updateStudyUrl(studyId)),
            )
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
            mockMvc.perform(get(findUrl(studyId))
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(studyReview.id.systematicStudyId.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should return 404 if don't find the study review`() {
            mockMvc.perform(get(findUrl("/-1"))
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should not find if user is unauthorized`(){
            val studyReview = factory.reviewDocument(systematicStudyId, idService.next())
            repository.insert(studyReview)

            testHelperService.testForUnauthorizedUser(mockMvc,
                get(findUrl("/${studyReview.id.studyReviewId}"))
            )
        }

        @Test
        fun `should not find if user is unauthenticated`(){
            val studyReview = factory.reviewDocument(systematicStudyId, idService.next())
            repository.insert(studyReview)

            testHelperService.testForUnauthenticatedUser(mockMvc,
                get(findUrl("/${studyReview.id.studyReviewId}")),
            )
        }

        @Test
        fun `should find all studies and return 200`() {
            repository.insert(factory.reviewDocument(systematicStudyId, idService.next(), searchSessionId, "study"))
            repository.insert(factory.reviewDocument(systematicStudyId, idService.next(), searchSessionId, "study"))
            repository.insert(factory.reviewDocument(UUID.randomUUID(), idService.next(), searchSessionId, "study"))

            mockMvc.perform(get(findUrl())
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.size").value(2))
        }

        @Test
        fun `should return empty list and return 200 if no study is found`() {
            mockMvc.perform(get(findUrl())
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.size").value(0))
                .andExpect(jsonPath("$.studyReviews").isEmpty())
        }

        @Test
        fun `should find all studies by source and return 200`() {
            repository.insert(
                factory.reviewDocument(
                    systematicStudyId, idService.next(), searchSessionId, "study",
                    sources = setOf("ACM")
                )
            )
            repository.insert(factory.reviewDocument(systematicStudyId, idService.next(), searchSessionId, "study"))
            repository.insert(
                factory.reviewDocument(
                    UUID.randomUUID(), idService.next(), searchSessionId, "study",
                    sources = setOf("ACM")
                )
            )

            mockMvc.perform(get(findBySourceUrl("ACM"))
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.size").value(1))
        }

        @Test
        fun `should find all studies by session and return 200`() {
            repository.insert(factory.reviewDocument(systematicStudyId, idService.next(), searchSessionId))
            repository.insert(factory.reviewDocument(systematicStudyId, idService.next(), UUID.randomUUID()))
            repository.insert(factory.reviewDocument(UUID.randomUUID(), idService.next(), UUID.randomUUID()))

            mockMvc.perform(get(findBySessionUrl(searchSessionId.toString()))
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.size").value(1))
        }

        @Test
        fun `should find all studies by author and return 200`() {
            repository.insert(factory.reviewDocument(systematicStudyId, idService.next(), searchSessionId))
            repository.insert(factory.reviewDocument(systematicStudyId, idService.next(), UUID.randomUUID(), authors = "Test"))
            repository.insert(factory.reviewDocument(UUID.randomUUID(), idService.next(), UUID.randomUUID()))

            mockMvc.perform(get(findByAuthorUrl("Marie Curie"))
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON)
            )
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
                patch(updateStatusStatus("selection-status"))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
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
                patch(updateStatusStatus("selection-status"))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
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

            val patchStatusStatus = updateStatusStatus("extraction-status")
            mockMvc.perform(patch(patchStatusStatus)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
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

            val patchStatusStatus = updateStatusStatus("extraction-status")
            mockMvc.perform(patch(patchStatusStatus)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON).content(json))
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
                patch(updateStatusStatus("reading-priority"))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
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

            val patchStatusStatus = updateStatusStatus("reading-priority")
            mockMvc.perform(patch(patchStatusStatus)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest)

            val studyReviewId = StudyReviewId(systematicStudyId, studyId)
            val updatedReview = repository.findById(studyReviewId).toNullable()
            assertEquals(studyReview, updatedReview)
        }

        @Test
        fun `should not update if user is unauthorized`() {
            val studyId = idService.next()

            testHelperService.testForUnauthorizedUser(mockMvc,
                patch(updateStatusStatus("reading-priority"))
                    .content(factory.validStatusUpdatePatchRequest(studyId, "HIGH"))
            )
        }

        @Test
        fun `should not update if user is unauthenticated`() {

            testHelperService.testForUnauthenticatedUser(mockMvc,
                patch(updateStatusStatus("reading-priority")),
            )
        }
    }

    @Nested
    @DisplayName("When answering ROB questions in a review")
    inner class AnswerRobQuestionsTests(
        @Autowired val questionRepository: MongoQuestionRepository
    ) {
        @Test
        fun `should assign answer to question and return 200`() {
            val studyId = idService.next()
            val questionId = UUID.randomUUID()

            val studyReview = factory.reviewDocument(systematicStudyId, studyId)
            repository.insert(studyReview)

            val question = factory.generateRobQuestionTextualDto(questionId, systematicStudyId = systematicStudyId)
            questionRepository.insert(question)

            val json = factory.validAnswerQuestionRequest(studyId, questionId, "TEXTUAL", "TEST")
            mockMvc.perform(
                patch(answerRiskOfBiasQuestion(studyId))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(json)
            )
                .andExpect(status().isOk)

            val studyReviewId = StudyReviewId(systematicStudyId, studyId)
            val updatedReview = repository.findById(studyReviewId)
            assertEquals(updatedReview.get().qualityAnswers[questionId], "TEST")
        }

        @Test
        fun `should not assign answer with invalid request`() {
            val studyId = idService.next()
            val questionId = UUID.randomUUID()

            val studyReview = factory.reviewDocument(systematicStudyId, studyId)
            repository.insert(studyReview)

            val question = factory.generateRobQuestionTextualDto(questionId, systematicStudyId = systematicStudyId)
            questionRepository.insert(question)

            val json = factory.invalidAnswerRiskOfBiasPatchRequest(studyId, questionId, "TEXTUAL")
            mockMvc.perform(
                patch(answerRiskOfBiasQuestion(studyId))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(json)
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `should not update if user is unauthorized`(){
            val studyId = idService.next()
            val questionId = UUID.randomUUID()


            testHelperService.testForUnauthorizedUser(
                mockMvc,
                patch(answerRiskOfBiasQuestion(studyId))
                    .content(factory.validAnswerQuestionRequest(
                        studyId, questionId, "TEXTUAL", "TEST"
                    ))
            )
        }

        @Test
        fun `should not update if user is unauthenticated`(){
            val studyId = idService.next()

            testHelperService.testForUnauthenticatedUser(mockMvc, patch(answerRiskOfBiasQuestion(studyId)),
            )
        }
    }

    @Nested
    @DisplayName("When answering Extraction questions in a review")
    inner class AnswerExtractionQuestionsTests(
        @Autowired val questionRepository: MongoQuestionRepository
    ) {
        @Test
        fun `should assign answer to question and return 200`() {
            val studyId = idService.next()
            val questionId = UUID.randomUUID()

            val studyReview = factory.reviewDocument(systematicStudyId, studyId)
            repository.insert(studyReview)

            val question = factory.generateExtractionQuestionTextualDto(questionId, systematicStudyId = systematicStudyId)
            questionRepository.insert(question)

            val json = factory.validAnswerQuestionRequest(studyId, questionId, "TEXTUAL", "TEST")
            mockMvc.perform(
                patch(answerExtractionQuestion(studyId))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(json)
            )
                .andExpect(status().isOk)

            val studyReviewId = StudyReviewId(systematicStudyId, studyId)
            val updatedReview = repository.findById(studyReviewId)
            assertEquals(updatedReview.get().formAnswers[questionId], "TEST")
        }

        @Test
        fun `should not assign answer with invalid request`() {
            val studyId = idService.next()
            val questionId = UUID.randomUUID()

            val studyReview = factory.reviewDocument(systematicStudyId, studyId)
            repository.insert(studyReview)

            val question = factory.generateExtractionQuestionTextualDto(questionId, systematicStudyId = systematicStudyId)
            questionRepository.insert(question)

            val json = factory.invalidAnswerRiskOfBiasPatchRequest(studyId, questionId, "TEXTUAL")
            mockMvc.perform(
                patch(answerExtractionQuestion(studyId))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(json)
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `should not update if user is unauthorized`(){
            val studyId = idService.next()
            val questionId = UUID.randomUUID()

            testHelperService.testForUnauthorizedUser(
                mockMvc,
                patch(answerExtractionQuestion(studyId))
                    .content(factory.validAnswerQuestionRequest(
                        studyId, questionId, "TEXTUAL", "TEST"
                    ))
            )
        }

        @Test
        fun `should not update if user is unauthenticated`(){
            val studyId = idService.next()

            testHelperService.testForUnauthenticatedUser(mockMvc, patch(answerExtractionQuestion(studyId)),
            )
        }
    }

    @Nested
    @DisplayName("When batch answering ROB questions in a review")
    inner class BatchAnswerRobQuestionsTests(
        @Autowired val questionRepository: MongoQuestionRepository
    ) {
        @Test
        @DisplayName("should save valid answers, ignore invalid ones, and return 200 with a detailed report")
        fun `should handle partial success correctly`() {
            val studyId = idService.next()
            val studyReview = factory.reviewDocument(systematicStudyId, studyId)
            repository.insert(studyReview)

            val validQId1 = UUID.randomUUID()
            val validQId2 = UUID.randomUUID()
            val invalidQId = UUID.randomUUID()

            val question1 = factory.generateRobQuestionTextualDto(validQId1, systematicStudyId)
            val question2 = factory.generateRobQuestionTextualDto(validQId2, systematicStudyId)
            val question3 = factory.generateRobQuestionNumberScaleDto(invalidQId, systematicStudyId)

            questionRepository.insert(question1)
            questionRepository.insert(question2)
            questionRepository.insert(question3)

            val jsonPayload = """
                {
                  "answers": [
                    { "questionId": "$validQId1", "type": "TEXTUAL", "answer": "First valid answer" },
                    { "questionId": "$validQId2", "type": "TEXTUAL", "answer": "Second valid answer" },
                    { "questionId": "$invalidQId", "type": "NUMBERED_SCALE", "answer": "This shouldn't be a string" }
                  ]
                }
            """.trimIndent()

            mockMvc.perform(
                patch(batchAnswerRiskOfBiasQuestion(studyId))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(jsonPayload)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.totalAnswered").value(2))
                .andExpect(jsonPath("$.succeededAnswers", hasSize<Any>(2)))
                .andExpect(jsonPath("$.succeededAnswers", hasItem(validQId1.toString())))
                .andExpect(jsonPath("$.succeededAnswers", hasItem(validQId2.toString())))
                .andExpect(jsonPath("$.failedAnswers", hasSize<Any>(1)))
                .andExpect(jsonPath("$.failedAnswers[0].questionId").value(invalidQId.toString()))
                .andExpect(jsonPath("$.failedAnswers[0].reason", containsString("not compatible with question type 'NUMBERED_SCALE'")))

            val updatedReview = repository.findById(StudyReviewId(systematicStudyId, studyId)).get()

            assertEquals(3, updatedReview.qualityAnswers.size)
            assertEquals("First valid answer", updatedReview.qualityAnswers[validQId1])
            assertEquals("Second valid answer", updatedReview.qualityAnswers[validQId2])
            assertNull(updatedReview.qualityAnswers[invalidQId])
        }

        @Test
        @DisplayName("should return 401 for unauthenticated users")
        fun `should not update if user is unauthenticated`() {
            testHelperService.testForUnauthenticatedUser(mockMvc, patch(batchAnswerRiskOfBiasQuestion(1L)))
        }

        @Test
        @DisplayName("should return 403 for unauthorized users")
        fun `should not update if user is unauthorized`() {
            val jsonPayload = """{ "answers": [] }"""
            testHelperService.testForUnauthorizedUser(
                mockMvc,
                patch(batchAnswerRiskOfBiasQuestion(1L)).content(jsonPayload)
            )
        }
    }

    @Nested
    @DisplayName("When batch answering Extraction questions in a review")
    inner class BatchAnswerExtractionQuestionsTests(
        @Autowired val questionRepository: MongoQuestionRepository
    ) {
        @Test
        @DisplayName("should save all valid answers and return 200 with no failures")
        fun `should handle full success correctly`() {
            val studyId = idService.next()
            val studyReview = factory.reviewDocument(systematicStudyId, studyId)
            repository.insert(studyReview)

            val textQId = UUID.randomUUID()
            val pickListQId = UUID.randomUUID()

            val question1 = factory.generateExtractionQuestionTextualDto(textQId, systematicStudyId = systematicStudyId)
            val question2 = factory.generateExtractionQuestionPickListDto(pickListQId, systematicStudyId = systematicStudyId)

            questionRepository.insert(question1)
            questionRepository.insert(question2)

            val jsonPayload = """
                {
                  "answers": [
                    { "questionId": "$textQId", "type": "TEXTUAL", "answer": "Another valid answer" },
                    { "questionId": "$pickListQId", "type": "PICK_LIST", "answer": "Option A" }
                  ]
                }
            """.trimIndent()

            mockMvc.perform(
                patch(batchAnswerExtractionQuestion(studyId))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(jsonPayload)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.totalAnswered").value(2))
                .andExpect(jsonPath("$.succeededAnswers", hasSize<Any>(2)))
                .andExpect(jsonPath("$.failedAnswers", hasSize<Any>(0)))

            val updatedReview = repository.findById(StudyReviewId(systematicStudyId, studyId)).get()
            assertEquals(3, updatedReview.formAnswers.size)
            assertEquals("Another valid answer", updatedReview.formAnswers[textQId])
            assertEquals("Option A", updatedReview.formAnswers[pickListQId])
        }

        @Test
        @DisplayName("should not save answers if the question context is wrong")
        fun `should fail on context mismatch`() {
            val studyId = idService.next()
            repository.insert(factory.reviewDocument(systematicStudyId, studyId))

            val extractionQId = UUID.randomUUID()
            questionRepository.insert(factory.generateExtractionQuestionTextualDto(extractionQId, systematicStudyId = systematicStudyId))

            val jsonPayload = """
                { "answers": [{ "questionId": "$extractionQId", "type": "TEXTUAL", "answer": "some answer" }] }
            """

            mockMvc.perform(
                patch(batchAnswerRiskOfBiasQuestion(studyId))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(jsonPayload)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.totalAnswered").value(0))
                .andExpect(jsonPath("$.failedAnswers", hasSize<Any>(1)))
                .andExpect(jsonPath("$.failedAnswers[0].questionId").value(extractionQId.toString()))
                .andExpect(jsonPath("$.failedAnswers[0].reason", containsString("Should answer question with the context: ROB")))
        }

        @Test
        @DisplayName("should return 401 for unauthenticated users")
        fun `should not update if user is unauthenticated`() {
            testHelperService.testForUnauthenticatedUser(mockMvc, patch(batchAnswerExtractionQuestion(1L)))
        }

        @Test
        @DisplayName("should return 403 for unauthorized users")
        fun `should not update if user is unauthorized`() {
            val jsonPayload = """{ "answers": [] }"""
            testHelperService.testForUnauthorizedUser(
                mockMvc,
                patch(batchAnswerExtractionQuestion(1L)).content(jsonPayload)
            )
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

            val duplicateIds = listOf(studyToDuplicateId)

            mockMvc.perform(
                patch(markAsDuplicatedUrl(studyToUpdateId))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(factory.validMarkAsDuplicateRequest(duplicateIds))
            )
                .andDo(print())
                .andExpect(status().isOk)

            val duplicateStudy = repository.findById(StudyReviewId(systematicStudyId, studyToDuplicateId)).toNullable()

            assertAll(
                { assertEquals("DUPLICATED", duplicateStudy?.selectionStatus) },
            )
        }


        @Test
        fun `should return 404 if study to be marked as duplicated (source) is not found`() {
            val studyToUpdateId = idService.next()
            val studyToDuplicateId = idService.next()

            val studyReviewToUpdate = factory.reviewDocument(systematicStudyId, studyToUpdateId)
            repository.insert(studyReviewToUpdate)

            val duplicateIds = listOf(studyToDuplicateId)

            mockMvc.perform(
                patch(markAsDuplicatedUrl(studyToUpdateId))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(factory.validMarkAsDuplicateRequest(duplicateIds))
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should return 404 if study to update (destination) is not found`() {
            val studyToUpdateId = idService.next()
            val studyToDuplicateId = idService.next()

            val studyReviewToDuplicate = factory.reviewDocument(systematicStudyId, studyToDuplicateId)
            repository.insert(studyReviewToDuplicate)

            val duplicateIds = listOf(studyToDuplicateId)

            mockMvc.perform(
                patch(markAsDuplicatedUrl(studyToUpdateId))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(factory.validMarkAsDuplicateRequest(duplicateIds))
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should not update if user is unauthorized`() {
            val studyToUpdateId = idService.next()
            val studyToDuplicateId = idService.next()

            val duplicateIds = listOf(studyToDuplicateId)

            testHelperService.testForUnauthorizedUser(
                mockMvc,
                patch(markAsDuplicatedUrl(studyToUpdateId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(factory.validMarkAsDuplicateRequest(duplicateIds))
            )
        }

        @Test
        fun `should not update if user is unauthenticated`() {
            val studyToUpdateId = idService.next()
            val studyToDuplicateId = idService.next()

            val duplicateIds = listOf(studyToDuplicateId)

            testHelperService.testForUnauthenticatedUser(
                mockMvc,
                patch(markAsDuplicatedUrl(studyToUpdateId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(factory.validMarkAsDuplicateRequest(duplicateIds))
            )
        }
    }
}