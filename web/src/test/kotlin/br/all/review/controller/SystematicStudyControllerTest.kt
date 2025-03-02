package br.all.review.controller

import br.all.application.protocol.repository.ProtocolDto
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.infrastructure.protocol.MongoProtocolRepository
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.review.SystematicStudyDocument
import br.all.infrastructure.shared.toNullable
import br.all.review.shared.TestDataFactory
import br.all.security.service.ApplicationUser
import br.all.shared.TestHelperService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
class SystematicStudyControllerTest(
    @Autowired private val repository: MongoSystematicStudyRepository,
    @Autowired private val testHelperService: TestHelperService,
    @Autowired private val mockMvc: MockMvc,
) {
    private lateinit var factory: TestDataFactory
    private lateinit var user: ApplicationUser
    
    @MockBean private lateinit var protocolRepository: MongoProtocolRepository

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        factory = TestDataFactory()
        user = testHelperService.createApplicationUser()
    }

    @AfterEach
    fun tearDown() {
        repository.deleteAll()
        testHelperService.deleteApplicationUser(user.id)
    }

    private fun postUrl() = "/api/v1/systematic-study"

    private fun getOneUrl(systematicStudyId: UUID = factory.systematicStudyId) =
        "/api/v1/systematic-study/$systematicStudyId"

    private fun getAllUrl() = "/api/v1/systematic-study"

    private fun getAllByOwnerUrl(ownerId: UUID = factory.ownerId) = "${getAllUrl()}/owner/$ownerId"

    private fun putUrl(systematicStudyId: UUID = factory.systematicStudyId) =
        "/api/v1/systematic-study/$systematicStudyId"

    @Nested
    @DisplayName("When posting a new Systematic Study")
    inner class WhenPostingANewSystematicStudy {

        @Test
        @Tag("ValidClasses")
        fun `should create a valid systematic study`() {
            val json = factory.createValidPostRequest()
            mockMvc.perform(
                post(postUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(json)
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.researcherId").value(user.id.toString()))
                .andExpect(jsonPath("$.systematicStudyId").isString)
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        @Tag("InvalidClasses")
        fun `should not create a invalid systematic study`() {
            val json = factory.createInvalidPostRequest()
            mockMvc.perform(post(postUrl())
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest)
        }
        
        @Test 
        fun `should not save systematic study if protocol creation fails`(){
            val json = factory.createValidPostRequest()

            Mockito.`when`(protocolRepository.save(Mockito.any())).thenThrow(RuntimeException())
                
            mockMvc.perform(
                post(postUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(json)
            ).andExpect(status().isBadRequest)
            
            assertEquals(emptyList<SystematicStudyDocument>(), repository.findAll())
        }

        @Test
        fun `should not create study when user is unauthorized`(){
            testHelperService.testForUnauthorizedUser(mockMvc,
                post(postUrl()).content(factory.createValidPostRequest())
            )
        }

        @Test
        fun `should not create study when user is unauthenticated`(){
            testHelperService.testForUnauthenticatedUser(mockMvc,
                post(postUrl()).content(factory.createValidPostRequest()),
            )
        }
    }

    @Nested
    @DisplayName("When getting systematic studies")
    inner class WhenGettingSystematicStudies {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And successfully finding them")
        inner class AndSuccessfullyFindingThem {
            @Test
            fun `should get a systematic study and return 200 status code`() {
                repository.save(factory.createSystematicStudyDocument(owner = user.id))
                mockMvc.perform(
                    get(getOneUrl())
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.content.id").value(factory.systematicStudyId.toString()))
                    .andExpect(jsonPath("$._links").exists())
            }

            @Test
            fun `should get the only study of the researcher when looking for all of them`() {
                repository.save(factory.createSystematicStudyDocument(owner = user.id))
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = UUID.randomUUID()
                    )
                )
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = UUID.randomUUID()
                    )
                )

                mockMvc.perform(get(getAllUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(1))
                    .andExpect(jsonPath("$._links").exists())
            }

            @Test
            fun `should get all systematic studies and return 200`() {
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = user.id
                    )
                )
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = user.id
                    )
                )
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = user.id
                    )
                )


                mockMvc.perform(get(getAllUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(3))
                    .andExpect(jsonPath("$._links").exists())
            }

            @Test
            fun `should get only the systematic studies that belongs to the researcher`() {
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = UUID.randomUUID()
                    )
                )
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = UUID.randomUUID()
                    )
                )
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = user.id
                    )
                )
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = user.id
                    )
                )
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = user.id
                    )
                )

                mockMvc.perform(get(getAllUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(3))
                    .andExpect(jsonPath("$._links").exists())
            }

            @Test
            fun `should get the only systematic study of a owner and return 200`() {
                saveOwnerStudy()
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = user.id
                    )
                )
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = user.id
                    )
                )

                mockMvc.perform(get(getAllByOwnerUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(1))
                    .andExpect(jsonPath("$.ownerId").value(factory.ownerId.toString()))
                    .andExpect(jsonPath("$._links").exists())
            }

            private fun saveOwnerStudy() = repository.save(
                factory.createSystematicStudyDocument(
                    id = UUID.randomUUID(),
                    owner = factory.ownerId,
                    collaborators = mutableSetOf(user.id),
                )
            )

            @Test
            fun `should get only systematic studies of the owner`() {
                repeat(3) {
                    saveOwnerStudy()
                    repository.save(
                        factory.createSystematicStudyDocument(
                            id = UUID.randomUUID(),
                            owner = user.id
                        )
                    )
                }

                mockMvc.perform(get(getAllByOwnerUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(3))
                    .andExpect(jsonPath("$.ownerId").value(factory.ownerId.toString()))
                    .andExpect(jsonPath("$._links").exists())
            }

            @Test
            fun `should get all systematic studies when they all belongs to the owner`() {
                repeat(6) { saveOwnerStudy() }
                mockMvc.perform(get(getAllByOwnerUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(6))
                    .andExpect(jsonPath("$.ownerId").value(factory.ownerId.toString()))
                    .andExpect(jsonPath("$._links").exists())
            }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And being unable to find any one")
        inner class AndBeingUnableToFindAnyOne {
            @Test
            fun `should return 404 when trying to find a nonexistent systematic study`() {
                mockMvc.perform(get(getOneUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isNotFound)
            }

            @Test
            fun `should a researcher is not a collaborator be unauthorized and return 403`() {
                testHelperService.testForUnauthenticatedUser(mockMvc, get(getOneUrl()),
                )
            }

            @Test
            fun `should a researcher is not a collaborator be unauthenticated and return 403`() {
                testHelperService.testForUnauthorizedUser(mockMvc, get(getOneUrl()))
            }

            @Test
            fun `should not get any systematic studies and return 200`() {
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = UUID.randomUUID()
                    )
                )
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = UUID.randomUUID()
                    )
                )
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = UUID.randomUUID()
                    )
                )

                mockMvc.perform(get(getAllUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(0))
                    .andExpect(jsonPath("$._links").exists())
            }

            @Test
            fun `should not get any systematic study if the given owner does not have any study`() {
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = UUID.randomUUID()
                    )
                )
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = UUID.randomUUID()
                    )
                )
                repository.save(
                    factory.createSystematicStudyDocument(
                        id = UUID.randomUUID(),
                        owner = UUID.randomUUID()
                    )
                )

                mockMvc.perform(get(getAllByOwnerUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(0))
                    .andExpect(jsonPath("$.ownerId").value(factory.ownerId.toString()))
                    .andExpect(jsonPath("$._links").exists())
            }
        }
    }

    @Nested
    @DisplayName("When updating systematic studies")
    inner class WhenUpdatingSystematicStudies {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And being succeed")
        inner class AndBeingSucceed {
            @ParameterizedTest
            @CsvSource("New title,", ",New description", "New title,New description")
            fun `should update the systematic study and return 200`(title: String?, description: String?) {
                val original =
                    factory.createSystematicStudyDocument(
                        title = "Old title",
                        description = "Old description",
                        owner = user.id
                    )
                repository.save(original)

                val request = factory.createValidPutRequest(title, description)
                mockMvc.perform(put(putUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(request)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.researcherId").value(user.id.toString()))
                    .andExpect(jsonPath("$.systematicStudyId").value(factory.systematicStudyId.toString()))
                    .andExpect(jsonPath("$._links").exists())

                assertNotEquals(original, repository.findById(factory.systematicStudyId).toNullable())
            }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("But failing to update")
        inner class ButFailingToUpdate {
            @Test
            fun `should nothing be updated if nothing is provided`() {
                val document = factory.createSystematicStudyDocument(owner = user.id)
                repository.save(document)

                val request = "{}"
                mockMvc.perform(put(putUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(request)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.researcherId").value(user.id.toString()))
                    .andExpect(jsonPath("$.systematicStudyId").value(factory.systematicStudyId.toString()))
                    .andExpect(jsonPath("$._links").exists())

                assertEquals(document, repository.findById(factory.systematicStudyId).toNullable())
            }

            @Test
            fun `should not update a systematic study if it does not exist and return 404`() {
                val request = factory.createValidPutRequest("New title", "New description")
                mockMvc.perform(put(putUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON).content(request)
                )
                    .andExpect(status().isNotFound)
            }

            @Test
            fun `should not update if user is unauthorized`(){
                testHelperService.testForUnauthorizedUser(mockMvc,
                    put(
                       putUrl()).content(factory.createValidPutRequest("New title", "New description"))
                )
            }

            @Test
            fun `should not update if user is unauthenticated`(){
                testHelperService.testForUnauthenticatedUser(mockMvc,
                    put(
                        putUrl()).content(factory.createValidPutRequest("New title", "New description")),
                )
            }
        }
    }
}