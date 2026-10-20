package br.all.agreement.controller

import br.all.infrastructure.agreement.AgreementVisibilityDocument
import br.all.infrastructure.agreement.MongoAgreementVisibilityRepository
import br.all.infrastructure.collaborator.CollaboratorDocument
import br.all.infrastructure.collaborator.MongoCollaboratorRepository
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.shared.toNullable
import br.all.security.service.ApplicationUser
import br.all.shared.TestHelperService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import br.all.review.shared.TestDataFactory as SystematicStudyTestDataFactory

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
@Tag("ControllerTest")
class AgreementVisibilityControllerTest @Autowired constructor(
    val repository: MongoAgreementVisibilityRepository,
    val systematicStudyRepository: MongoSystematicStudyRepository,
    val collaboratorRepository: MongoCollaboratorRepository,
    private val testHelperService: TestHelperService,
    val mockMvc: MockMvc,
) {

    private val systematicStudyFactory = SystematicStudyTestDataFactory()

    private lateinit var user: ApplicationUser
    private var otherUser: ApplicationUser? = null
    private lateinit var systematicStudyId: UUID

    fun url(id: UUID = systematicStudyId) =
        "/api/v1/systematic-study/$id/agreement/visibility"

    fun body(stage: String, visibility: String) =
        """{ "stage": "$stage", "visibility": "$visibility" }"""

    private fun createOtherCollaborator(): ApplicationUser {
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

        systematicStudyId = UUID.randomUUID()
        user = testHelperService.createApplicationUser()

        systematicStudyRepository.deleteAll()
        systematicStudyRepository.save(
            systematicStudyFactory.createSystematicStudyDocument(
                id = systematicStudyId,
                owner = user.id,
            )
        )

        collaboratorRepository.deleteAll()
        collaboratorRepository.save(
            systematicStudyFactory.createCollaboratorDocument(user, systematicStudyId)
        )
    }

    @AfterEach
    fun teardown() {
        repository.deleteAll()
        systematicStudyRepository.deleteAll()
        collaboratorRepository.deleteAll()
        testHelperService.deleteApplicationUser(user.id)
        otherUser?.let { testHelperService.deleteApplicationUser(it.id) }
        otherUser = null
    }

    @Nested
    @DisplayName("When getting the agreement visibility")
    inner class GetTests {

        @Test
        fun `should return the default visibility when never configured and return 200`() {
            mockMvc.perform(
                get(url())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.selection").value("FINISHED_ONLY"))
                .andExpect(jsonPath("$.extraction").value("FINISHED_ONLY"))
        }

        @Test
        fun `should return the saved visibility of each stage and return 200`() {
            repository.save(
                AgreementVisibilityDocument(systematicStudyId, "ALL", "NONE")
            )

            mockMvc.perform(
                get(url())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.selection").value("ALL"))
                .andExpect(jsonPath("$.extraction").value("NONE"))
        }

        @Test
        fun `should allow a collaborator who is not the owner to read the visibility`() {
            val collaborator = createOtherCollaborator()
            repository.save(
                AgreementVisibilityDocument(systematicStudyId, "NONE", "ALL")
            )

            mockMvc.perform(
                get(url())
                    .with(SecurityMockMvcRequestPostProcessors.user(collaborator))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.selection").value("NONE"))
                .andExpect(jsonPath("$.extraction").value("ALL"))
        }

        @Test
        fun `should not find the visibility of a nonexistent systematic study and return 404`() {
            mockMvc.perform(
                get(url(UUID.randomUUID()))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should not get the visibility if user is unauthenticated`() {
            testHelperService.testForUnauthenticatedUser(mockMvc, get(url()))
        }

        @Test
        fun `should not get the visibility if user is unauthorized`() {
            testHelperService.testForUnauthorizedUser(mockMvc, get(url()))
        }
    }

    @Nested
    @DisplayName("When changing the agreement visibility")
    inner class PutTests {

        @Test
        fun `owner should change the visibility of the selection stage and return 200`() {
            mockMvc.perform(
                put(url())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body("SELECTION", "ALL"))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.selection").value("ALL"))
                .andExpect(jsonPath("$.extraction").value("FINISHED_ONLY"))

            val saved = repository.findById(systematicStudyId).toNullable()
            assertNotNull(saved)
            assertEquals("ALL", saved!!.selection)
            assertEquals("FINISHED_ONLY", saved.extraction)
        }

        @Test
        fun `owner should change the visibility of the extraction stage and return 200`() {
            mockMvc.perform(
                put(url())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body("EXTRACTION", "NONE"))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.selection").value("FINISHED_ONLY"))
                .andExpect(jsonPath("$.extraction").value("NONE"))
        }

        @Test
        fun `should keep the other stage when changing only one of them`() {
            repository.save(
                AgreementVisibilityDocument(systematicStudyId, "ALL", "NONE")
            )

            mockMvc.perform(
                put(url())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body("EXTRACTION", "FINISHED_ONLY"))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.selection").value("ALL"))
                .andExpect(jsonPath("$.extraction").value("FINISHED_ONLY"))

            val saved = repository.findById(systematicStudyId).toNullable()
            assertEquals("ALL", saved!!.selection)
            assertEquals("FINISHED_ONLY", saved.extraction)
        }

        @Test
        fun `the new visibility should be returned by a following get`() {
            mockMvc.perform(
                put(url())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body("SELECTION", "NONE"))
            )
                .andExpect(status().isOk)

            mockMvc.perform(
                get(url())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.selection").value("NONE"))
                .andExpect(jsonPath("$.extraction").value("FINISHED_ONLY"))
        }

        @Test
        fun `should not allow a collaborator who is not the owner to change the visibility and return 403`() {
            val collaborator = createOtherCollaborator()

            mockMvc.perform(
                put(url())
                    .with(SecurityMockMvcRequestPostProcessors.user(collaborator))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body("SELECTION", "ALL"))
            )
                .andExpect(status().isForbidden)

            assertTrue(repository.findAll().isEmpty())
        }

        @Test
        fun `should not change the visibility of a nonexistent systematic study and return 404`() {
            mockMvc.perform(
                put(url(UUID.randomUUID()))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body("SELECTION", "ALL"))
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should not change the visibility with an invalid visibility and return 400`() {
            mockMvc.perform(
                put(url())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body("SELECTION", "EVERYONE"))
            )
                .andExpect(status().isBadRequest)

            assertTrue(repository.findAll().isEmpty())
        }

        @Test
        fun `should not change the visibility with an invalid stage and return 400`() {
            mockMvc.perform(
                put(url())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body("IDENTIFICATION", "ALL"))
            )
                .andExpect(status().isBadRequest)

            assertTrue(repository.findAll().isEmpty())
        }

        @Test
        fun `should not change the visibility when the visibility is missing and return 400`() {
            mockMvc.perform(
                put(url())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{ "stage": "SELECTION" }""")
            )
                .andExpect(status().isBadRequest)

            assertTrue(repository.findAll().isEmpty())
        }

        @Test
        fun `should not change the visibility if user is unauthenticated`() {
            testHelperService.testForUnauthenticatedUser(
                mockMvc,
                put(url()).content(body("SELECTION", "ALL"))
            )
        }

        @Test
        fun `should not change the visibility if user is unauthorized`() {
            testHelperService.testForUnauthorizedUser(
                mockMvc,
                put(url()).content(body("SELECTION", "ALL"))
            )
        }
    }
}