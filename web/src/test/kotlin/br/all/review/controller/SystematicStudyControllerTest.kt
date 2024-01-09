package br.all.review.controller

import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.review.shared.TestDataFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
class SystematicStudyControllerTest(
    @Autowired private val repository: MongoSystematicStudyRepository,
    @Autowired private val mockMvc: MockMvc,
) {
    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        factory = TestDataFactory()
    }

    @AfterEach
    fun tearDown() = repository.deleteAll()

    private fun postUrl() = "/api/v1/researcher/${factory.researcherId}/systematic-study"

    private fun getOneUrl(
        researcherId: UUID = factory.researcherId,
        systematicStudyId: UUID = factory.systematicStudyId,
    ) = "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId"

    private fun getAllUrl(
        researcherId: UUID = factory.researcherId,
    ) =  "/api/v1/researcher/$researcherId/systematic-study"

    private fun getAllByOwnerUrl(
        researcherId: UUID = factory.researcherId,
        ownerId: UUID = factory.ownerId,
    ) = "${getAllUrl(researcherId)}/owner/$ownerId"

    @Nested
    @DisplayName("When posting a new Systematic Study")
    inner class WhenPostingANewSystematicStudy {
        @Test
        @Tag("ValidClasses")
        fun `should create a valid systematic study`() {
            val json = factory.createValidPostRequest()
            mockMvc.perform(post(postUrl()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.researcherId").value(factory.researcherId.toString()))
                .andExpect(jsonPath("$.systematicStudyId").isString)
                .andExpect(jsonPath("$._links").exists())
        }
        
        @Test
        @Tag("InvalidClasses")
        fun `should not create a invalid systematic study`() {
            val json = factory.createInvalidPostRequest()
            mockMvc.perform(post(postUrl()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest)
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
                repository.save(factory.createSystematicStudyDocument())
                mockMvc.perform(get(getOneUrl()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.content.id").value(factory.systematicStudyId.toString()))
                    .andExpect(jsonPath("$._links").exists())
            }

            @Test
            fun `should get the only study of the researcher when looking for all of them`() {
                repository.save(factory.createSystematicStudyDocument())
                repository.save(factory.createSystematicStudyDocument(id = UUID.randomUUID(), owner = UUID.randomUUID()))
                repository.save(factory.createSystematicStudyDocument(id = UUID.randomUUID(), owner = UUID.randomUUID()))

                mockMvc.perform(get(getAllUrl()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(1))
                    .andExpect(jsonPath("$._links").exists())
            }

            @Test
            fun `should get all systematic studies and return 200`() {
                repository.save(factory.createSystematicStudyDocument(id = UUID.randomUUID()))
                repository.save(factory.createSystematicStudyDocument(id = UUID.randomUUID()))
                repository.save(factory.createSystematicStudyDocument(id = UUID.randomUUID()))

                mockMvc.perform(get(getAllUrl()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(3))
                    .andExpect(jsonPath("$._links").exists())
            }

            @Test
            fun `should get only the systematic studies that belongs to the researcher`() {
                repository.save(factory.createSystematicStudyDocument(id = UUID.randomUUID()))
                repository.save(factory.createSystematicStudyDocument(id = UUID.randomUUID()))
                repository.save(factory.createSystematicStudyDocument(id = UUID.randomUUID()))
                repository.save(factory.createSystematicStudyDocument(id = UUID.randomUUID(), owner = UUID.randomUUID()))
                repository.save(factory.createSystematicStudyDocument(id = UUID.randomUUID(), owner = UUID.randomUUID()))

                mockMvc.perform(get(getAllUrl()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(3))
                    .andExpect(jsonPath("$._links").exists())
            }

            @Test
            fun `should get the only systematic study of a owner and return 200`() {
                saveOwnerStudy()
                repository.save(factory.createSystematicStudyDocument(id = UUID.randomUUID(), owner = factory.researcherId))
                repository.save(factory.createSystematicStudyDocument(id = UUID.randomUUID(), owner = factory.researcherId))

                mockMvc.perform(get(getAllByOwnerUrl()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(1))
                    .andExpect(jsonPath("$.ownerId").value(factory.ownerId.toString()))
                    .andExpect(jsonPath("$._links").exists())
            }

            private fun saveOwnerStudy() = repository.save(
                factory.createSystematicStudyDocument(
                    id = UUID.randomUUID(),
                    owner = factory.ownerId,
                    collaborators = mutableSetOf(factory.researcherId),
                )
            )

            @Test
            fun `should get only systematic studies of the owner`() {
                repeat(3) {
                    saveOwnerStudy()
                    repository.save(factory.createSystematicStudyDocument(id = UUID.randomUUID(), owner = factory.researcherId))
                }

                mockMvc.perform(get(getAllByOwnerUrl()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(3))
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
                mockMvc.perform(get(getOneUrl()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound)
            }

            @Test
            fun `should a researcher is not a collaborator be unauthorized and return 403`() {
                repository.save(factory.createSystematicStudyDocument())
                val notAllowed = UUID.randomUUID()
                mockMvc.perform(get(getOneUrl(researcherId = notAllowed)).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden)
            }

            @Test
            fun `should not get any systematic studies and return 200`() {
                repository.save(factory.createSystematicStudyDocument())
                repository.save(factory.createSystematicStudyDocument())
                repository.save(factory.createSystematicStudyDocument())

                mockMvc.perform(get(getAllUrl(UUID.randomUUID())).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.size").value(0))
                    .andExpect(jsonPath("$._links").exists())
            }
        }
    }
}