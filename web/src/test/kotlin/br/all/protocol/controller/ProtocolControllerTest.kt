package br.all.protocol.controller

import br.all.application.protocol.repository.CriterionDto
import br.all.infrastructure.collaboration.MongoCollaborationRepository
import br.all.infrastructure.protocol.MongoProtocolRepository
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.review.SystematicStudyDocument
import br.all.protocol.shared.TestDataFactory
import br.all.security.service.ApplicationUser
import br.all.shared.TestHelperService
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
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
@DisplayName("Protocol Controller Integration Tests")
class ProtocolControllerTest(
    @Autowired private val protocolRepository: MongoProtocolRepository,
    @Autowired private val systematicStudyRepository: MongoSystematicStudyRepository,
    @Autowired val collaborationRepository: MongoCollaborationRepository,
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val testHelperService: TestHelperService
) {
    private lateinit var user: ApplicationUser
    private lateinit var systematicStudy: SystematicStudyDocument
    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyDataFactory: SystematicStudyTestDataFactory

    @BeforeEach
    fun setUp()  {
        protocolRepository.deleteAll()
        systematicStudyRepository.deleteAll()

        factory = TestDataFactory()
        systematicStudyDataFactory = SystematicStudyTestDataFactory()

        user = testHelperService.createApplicationUser()

        val (_, systematicStudyId) = factory
        systematicStudy = systematicStudyDataFactory.createSystematicStudyDocument(
            id = systematicStudyId,
            collaborators = mutableSetOf(user.id)
        )
        systematicStudyRepository.save(systematicStudy)
        collaborationRepository.deleteAll()
        collaborationRepository.save(
            br.all.review.shared.TestDataFactory().createCollaborationDocument(
                systematicStudyId = systematicStudyId,
                researcherId = user.id
            )
        )
    }

    @AfterEach
    fun tearDown() {
        protocolRepository.deleteAll()
        systematicStudyRepository.deleteAll()
        testHelperService.deleteApplicationUser(user.id)
    }

    private fun getUrl(systematicStudy: UUID = factory.protocol) =
        "/systematic-study/$systematicStudy/protocol"

    private fun putUrl(systematicStudyId: UUID = factory.protocol) =
        "/systematic-study/$systematicStudyId/protocol"

    private fun getStage(systematicStudy: UUID = factory.protocol) =
        "/systematic-study/$systematicStudy/protocol/stage"

    @Nested
    @DisplayName("When getting protocols")
    inner class WhenGettingProtocols {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And finding them")
        inner class AndFindingThem {
            @Test
            fun `should find the protocol and return it as well as 200 status code`() {
                val document = factory.createProtocolDocument()
                protocolRepository.save(document)

                mockMvc.perform(get(getUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.systematicStudyId").value(factory.protocol.toString()))
                    .andExpect(jsonPath("$.content").exists())
                    .andExpect(jsonPath("$._links").exists())
            }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And failing to get any")
        inner class AndFailingToGetAny {
            @Test
            fun `should return 404 when trying to find nonexistent protocols`() {
                mockMvc.perform(get(getUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isNotFound)
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.detail").exists())
            }

            @Test
            fun `should return 404 when trying to find protocols of nonexistent systematic studies`() {
                mockMvc.perform(
                    get(getUrl(systematicStudy = UUID.randomUUID()))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound)
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.detail").exists())
            }

            @Test
            fun `should not authorize researchers that are not a collaborator to find protocols`() {
                testHelperService.testForUnauthorizedUser(mockMvc, get(getUrl()))
            }

            @Test
            fun `should not allow unauthenticated users to find protocols`() {
                testHelperService.testForUnauthenticatedUser(mockMvc, get(getUrl()))
            }
        }
    }

    @Nested
    @DisplayName("When putting protocols")
    inner class WhenPuttingProtocols {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And updating the protocol successfully")
        inner class AndUpdatingTheProtocolSuccessfully {
            @Test
            fun `should update an existing protocol`() {
                val document = factory.createProtocolDocument()
                val json = factory.validPutRequest()

                protocolRepository.save(document)

                mockMvc.perform(put(putUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.systematicStudyId").exists())
                    .andExpect(jsonPath("$._links").exists())
            }

            @Test
            fun `should update an existing protocol without deleting existing collection-type variables`() {
                val document = factory.createProtocolDocument(
                    keywords = setOf("keyword1", "keyword2"),
                    robQuestions = setOf(UUID.randomUUID(), UUID.randomUUID()),
                    selectionCriteria = setOf(CriterionDto("desc", "INCLUSION"))
                )
                val json = factory.validPutRequest()

                protocolRepository.save(document)

                mockMvc.perform(put(putUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.systematicStudyId").exists())
                    .andExpect(jsonPath("$._links").exists())

                val updated = protocolRepository.findById(document.id).get()
                assert(updated.keywords.isNotEmpty()
                        && updated.selectionCriteria.isNotEmpty()
                        && updated.robQuestions.isNotEmpty())
            }

            @Test
            fun `should update an existing protocol without losing sourcesSelectionCriteria`() {
                val document = factory.createProtocolDocument()
                val json = factory.validPutRequest()

                protocolRepository.save(document)

                mockMvc.perform(put(putUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.systematicStudyId").exists())
                    .andExpect(jsonPath("$._links").exists())

                val updated = protocolRepository.findById(document.id).get()
                assert(updated.sourcesSelectionCriteria != null)
            }

            @Test
            fun `should update an existing protocol without replacing question ids`() {
                val document = factory.createProtocolDocument()
                val json = factory.validPutRequest()

                protocolRepository.save(document)

                mockMvc.perform(put(putUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.systematicStudyId").exists())
                    .andExpect(jsonPath("$._links").exists())

                val updated = protocolRepository.findById(document.id).get()
                assert(updated.extractionQuestions == document.extractionQuestions)
                assert(updated.robQuestions == document.robQuestions)
            }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And failing to perform changes")
        inner class AndFailingToPerformChanges {
            @Test
            fun `should not be possible to update the protocol of a nonexistent systematic study`() {
                val nonexistentStudy = UUID.randomUUID()
                val json = factory.validPutRequest()

                mockMvc.perform(
                    put(putUrl(systematicStudyId = nonexistentStudy))
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().isNotFound)
            }

            @Test
            fun `should not allow researchers that are not collaborators to update the protocol`() {
                testHelperService.testForUnauthorizedUser(
                    mockMvc,
                    put(putUrl()).content(factory.validPutRequest())
                )
            }

            @Test
            fun `should not allow unauthenticated users to update the protocol`() {
                testHelperService.testForUnauthenticatedUser(
                    mockMvc,
                    put(putUrl()).content(factory.validPutRequest())
                )
            }
        }
    }

    @Nested
    @DisplayName("When getting protocol stage")
    inner class WhenGettingProtocolStage {
        @Test
        fun `should return protocol stage with 200 status when protocol exists`() {
            val document = factory.createProtocolDocument()
            protocolRepository.save(document)

            mockMvc.perform(get(getStage())
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(factory.protocol.toString()))
                .andExpect(jsonPath("$.currentStage").exists())
        }

        @Test
        fun `should return 404 when protocol does not exist`() {
            mockMvc.perform(get(getStage())
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.detail").exists())
        }

        @Test
        fun `should not authorize researchers that are not collaborators to get protocol stage`() {
            testHelperService.testForUnauthorizedUser(mockMvc, get(getStage()))
        }

        @Test
        fun `should not allow unauthenticated users to get protocol stage`() {
            testHelperService.testForUnauthenticatedUser(mockMvc, get(getStage()))
        }
    }
}