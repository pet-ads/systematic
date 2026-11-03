package br.all.agreement.controller

import br.all.domain.model.study.StudyReviewStage
import br.all.infrastructure.collaborator.CollaboratorDocument
import br.all.infrastructure.collaborator.MongoCollaboratorRepository
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.reviewmode.MongoReviewerStudyRepository
import br.all.infrastructure.shared.toNullable
import br.all.infrastructure.study.MongoStudyReviewRepository
import br.all.infrastructure.study.StudyReviewId
import br.all.infrastructure.study.StudyReviewIdGeneratorService
import br.all.security.service.ApplicationUser
import br.all.shared.TestHelperService
import br.all.study.utils.TestDataFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import br.all.review.shared.TestDataFactory as SystematicStudyTestDataFactory

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
@Tag("ControllerTest")
class AgreementStudyReviewControllerTest @Autowired constructor(
    val repository: MongoStudyReviewRepository,
    val reviewerStudyRepository: MongoReviewerStudyRepository,
    val systematicStudyRepository: MongoSystematicStudyRepository,
    val collaboratorRepository: MongoCollaboratorRepository,
    private val testHelperService: TestHelperService,
    val idService: StudyReviewIdGeneratorService,
    val mockMvc: MockMvc,
) {

    private val systematicStudyFactory = SystematicStudyTestDataFactory()
    private lateinit var factory: TestDataFactory
    private lateinit var user: ApplicationUser
    private var otherUser: ApplicationUser? = null
    private lateinit var systematicStudyId: UUID

    private fun url(path: String, id: UUID = systematicStudyId) =
        "/api/v1/systematic-study/$id/agreement/study-review/$path"

    private fun statusJson(studyId: Long, status: String) =
        """{ "studyReviewId": [$studyId], "status": "$status", "criteria": [] }"""

    private fun originalOf(studyId: Long) = repository.findById(StudyReviewId(systematicStudyId, studyId)).toNullable()

    private fun createReviewerCollaborator(): ApplicationUser {
        val collaborator = testHelperService.createApplicationUser()
        otherUser = collaborator
        systematicStudyRepository.deleteAll()
        systematicStudyRepository.save(
            systematicStudyFactory.createSystematicStudyDocument(
                id = systematicStudyId,
                owner = user.id,
                collaborators = mutableSetOf(collaborator.id),
            )
        )
        collaboratorRepository.save(
            CollaboratorDocument(
                CollaboratorDocument.buildId(collaborator.id, systematicStudyId),
                collaborator.id,
                systematicStudyId,
                collaborator.username,
                "reviewer@test.com",
                "REVIEWER",
            )
        )
        return collaborator
    }

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        reviewerStudyRepository.deleteAll()
        collaboratorRepository.deleteAll()

        factory = TestDataFactory()
        systematicStudyId = factory.systematicStudyId
        idService.reset(systematicStudyId)

        user = testHelperService.createApplicationUser()

        systematicStudyRepository.deleteAll()
        systematicStudyRepository.save(
            systematicStudyFactory.createSystematicStudyDocument(id = systematicStudyId, owner = user.id)
        )
        collaboratorRepository.save(systematicStudyFactory.createCollaboratorDocument(user, systematicStudyId)) // OWNER
    }

    @AfterEach
    fun teardown() {
        repository.deleteAll()
        reviewerStudyRepository.deleteAll()
        collaboratorRepository.deleteAll()
        systematicStudyRepository.deleteAll()
        testHelperService.deleteApplicationUser(user.id)
        otherUser?.let { testHelperService.deleteApplicationUser(it.id) }
        otherUser = null
    }

    @Nested
    @DisplayName("When resolving in the agreement")
    inner class ResolveTests {

        @Test
        fun `owner should resolve the selection status of the original study and return 200`() {
            val studyId = idService.next(systematicStudyId)
            repository.insert(factory.reviewDocument(systematicStudyId, studyId))
            val reviewerCopy = factory.reviewerStudyDocument(systematicStudyId, studyId, UUID.randomUUID(), StudyReviewStage.SELECTION)
            reviewerStudyRepository.insert(reviewerCopy)

            mockMvc.perform(
                patch(url("selection-status"))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(statusJson(studyId, "INCLUDED"))
            ).andExpect(status().isOk)

            assertEquals("INCLUDED", originalOf(studyId)?.selectionStatus)
            // a cópia do revisor não é tocada
            assertEquals(reviewerCopy, reviewerStudyRepository.findById(reviewerCopy.id).toNullable())
        }

        @Test
        fun `owner should change the reading priority of the original study and return 200`() {
            val studyId = idService.next(systematicStudyId)
            repository.insert(factory.reviewDocument(systematicStudyId, studyId))

            mockMvc.perform(
                patch(url("reading-priority"))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(statusJson(studyId, "VERY_HIGH"))
            ).andExpect(status().isOk)

            assertEquals("VERY_HIGH", originalOf(studyId)?.readingPriority)
        }

        @Test
        fun `should ignore a reviewerId sent in the body and still act on the original study`() {
            val studyId = idService.next(systematicStudyId)
            repository.insert(factory.reviewDocument(systematicStudyId, studyId))

            mockMvc.perform(
                patch(url("selection-status"))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """{ "studyReviewId": [$studyId], "status": "EXCLUDED", "criteria": [],
                             "reviewerId": "${UUID.randomUUID()}" }"""
                    )
            ).andExpect(status().isOk)

            assertEquals("EXCLUDED", originalOf(studyId)?.selectionStatus)
        }

        @Test
        fun `should not allow a reviewer to resolve in the agreement and return 403`() {
            val studyId = idService.next(systematicStudyId)
            val original = factory.reviewDocument(systematicStudyId, studyId)
            repository.insert(original)
            val reviewer = createReviewerCollaborator()

            mockMvc.perform(
                patch(url("selection-status"))
                    .with(SecurityMockMvcRequestPostProcessors.user(reviewer))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(statusJson(studyId, "INCLUDED"))
            ).andExpect(status().isForbidden)

            assertEquals(original, originalOf(studyId))
        }

        @Test
        fun `should return 404 when the original study does not exist`() {
            mockMvc.perform(
                patch(url("selection-status"))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(statusJson(999L, "INCLUDED"))
            ).andExpect(status().isNotFound)
        }

        @Test
        fun `should return 404 when the systematic study does not exist`() {
            mockMvc.perform(
                patch(url("selection-status", UUID.randomUUID()))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(statusJson(1L, "INCLUDED"))
            ).andExpect(status().isNotFound)
        }

        @Test
        fun `should not resolve if user is unauthenticated`() {
            testHelperService.testForUnauthenticatedUser(
                mockMvc, patch(url("selection-status")).content(statusJson(1L, "INCLUDED"))
            )
        }

        @Test
        fun `should not resolve if user is unauthorized`() {
            testHelperService.testForUnauthorizedUser(
                mockMvc, patch(url("selection-status")).content(statusJson(1L, "INCLUDED"))
            )
        }
    }
}