package br.all.search.controller

import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.search.MongoSearchSessionRepository
import br.all.infrastructure.shared.toNullable
import br.all.infrastructure.study.MongoStudyReviewRepository
import br.all.infrastructure.study.StudyReviewIdGeneratorService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class SearchSessionControllerTest(
    @Autowired val repository: MongoSearchSessionRepository,
    @Autowired val systematicStudyRepository: MongoSystematicStudyRepository,
    @Autowired val studyReviewRepository: MongoStudyReviewRepository,
    @Autowired val idService: StudyReviewIdGeneratorService,
    @Autowired val mockMvc: MockMvc,
) {

    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyId: UUID
    private lateinit var researcherId: UUID

    fun postUrl() = "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/search-session"
    fun findUrl(sessionId: String = "") =
        "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/search-session${sessionId}"
    fun invalidFindUrl(researcherId: String = "", sessionId: String = "") =
        "/api/v1/researcher/${researcherId}/systematic-study/$systematicStudyId/search-session${sessionId}"

    fun putUrl(
        researcherId: UUID = factory.researcherId,
        systematicStudyId: UUID = factory.systematicStudyId,
        searchSessionId: UUID = factory.sessionId
    ) = "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/search-session/$searchSessionId"

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        repository.deleteAll()
        systematicStudyId = factory.systematicStudyId
        researcherId = factory.researcherId

        systematicStudyRepository.save(
            br.all.review.shared.TestDataFactory().createSystematicStudyDocument(
                id = systematicStudyId,
                owner = researcherId,
            )
        )
    }

    @AfterEach
    fun teardown() {
        repository.deleteAll()
        systematicStudyRepository.deleteAll()
        studyReviewRepository.deleteAll()
        idService.reset()
    }

    @Nested
    @DisplayName("When creating a search session")
    inner class CreateTests {
        @Test
        fun `should create search session and return 201`() {
            mockMvc.perform(multipart(postUrl()).file(factory.bibfile()).param("data", factory.validPostRequest()))
                .andExpect(status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.researcherId").value(researcherId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sessionId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links").exists())
                .andReturn()
        }

        @Test
        fun `should return 400 when BibTeX format is invalid`() {

            mockMvc.perform(
                multipart(postUrl())
                    .file("bibFile", factory.invalidBibFile())
                    .param("data", factory.validPostRequest())
            )
                .andExpect(status().isBadRequest)
                .andReturn()
        }

        @Test
        fun `should return 403 when researcher is not authorized`() {
            val unauthorizedResearcherId = UUID.randomUUID()

            mockMvc.perform(
                multipart(postUrl()).file(factory.bibfile())
                    .param("data", factory.validPostRequest(unauthorizedResearcherId))
            )
                .andExpect(status().isForbidden)
                .andReturn()
        }

        @Test
        fun `should return 404 for invalid request body`() {

            mockMvc.perform(multipart(postUrl()).file(factory.bibfile()).param("data", factory.invalidPostRequest()))
                .andExpect(status().isNotFound)
                .andReturn()
        }
    }

    @Nested
    @DisplayName("When finding a search session")
    inner class FindTests {
        @Test
        fun `should find the search session and return 200`() {

            val searchSession = factory.searchSessionDocument(factory.sessionId, systematicStudyId)
            repository.insert(searchSession)

            val sessionId = "/${searchSession.id}"
            mockMvc.perform(MockMvcRequestBuilders.get(findUrl(sessionId)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(searchSession.id.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links").exists())
        }

        @Test
        fun `should return 403 when researcher is not authorized`() {

            val searchSession = factory.searchSessionDocument(factory.sessionId, systematicStudyId)
            repository.insert(searchSession)

            val sessionId = "/${searchSession.id}"
            val researcherId = "/${factory.invalidResearcherId}"
            mockMvc.perform(MockMvcRequestBuilders.get(
                invalidFindUrl(researcherId, sessionId
                )).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden)
                .andReturn()
        }

        @Test
        fun `should return 404 if don't find the search session`() {
            mockMvc.perform(
                MockMvcRequestBuilders.get(findUrl(factory.nonExistentSessionId.toString()))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should return 400 if search session id is in a invalid format`() {
            mockMvc.perform(MockMvcRequestBuilders.get(findUrl("/-1")).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `should find all the search sessions and return 200`() {
            val id1 = UUID.randomUUID()
            val id2 = UUID.randomUUID()
            val id3 = UUID.randomUUID()
            val wrongSystematicStudyId = UUID.randomUUID()

            repository.insert(factory.searchSessionDocument(id1, systematicStudyId))
            repository.insert(factory.searchSessionDocument(id2, systematicStudyId))
            repository.insert(factory.searchSessionDocument(id3, wrongSystematicStudyId))

            mockMvc.perform(MockMvcRequestBuilders.get(findUrl()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(2))
        }

        @Test
        fun `should return empty list and return 200 if no search session is found`() {
            mockMvc.perform(MockMvcRequestBuilders.get(findUrl()).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.searchSessions").isEmpty())
        }
    }

    @Nested
    @DisplayName("When updating search session")
    inner class WhenUpdatingSearchSession {
        @ParameterizedTest
        @CsvSource(
            "New Search String, ,",
            " ,New Additional Info,",
            " , , New Search Source",
            "New Search String,New Additional Info, New Search Source")
        fun `should update the search session and return 200`(
            searchString: String?, additionalInfo: String?, searchSource: String?
        ) {
            val original = factory.searchSessionDocument(
                searchString = "Old Search String",
                additionalInfo = "Old Additional Info",
                searchSource = "Old Search Source"
            )
            repository.insert(original)

            val request = factory.createValidPutRequest(searchString, additionalInfo, searchSource)
            mockMvc.perform(put(putUrl()).contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.researcherId").value(factory.researcherId.toString()))
                .andExpect(jsonPath("$.systematicStudyId").value(factory.systematicStudyId.toString()))
                .andExpect(jsonPath("$.searchSessionID").value(factory.sessionId.toString()))
                .andExpect(jsonPath("$._links").exists())

                val new = repository.findById(factory.sessionId).toNullable()

                assertNotEquals(original, repository.findById(factory.sessionId).toNullable())
        }

        @Test
        fun `should nothing be updated if nothing is provided`(){
            val document = factory.searchSessionDocument()
            repository.insert(document)

            val request = "{}"
            mockMvc.perform(put(putUrl()).contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.researcherId").value(factory.researcherId.toString()))
                .andExpect(jsonPath("$.systematicStudyId").value(factory.systematicStudyId.toString()))
                .andExpect(jsonPath("$.searchSessionID").value(factory.sessionId.toString()))
                .andExpect(jsonPath("$._links").exists())

            assertEquals(document, repository.findById(factory.sessionId).toNullable())
        }

        @Test
        fun `should not update a search session if it does not exist and return 404`() {
            val request = factory.createValidPutRequest(
                "New Search String", "New Additional Info", "New SearchSource"
            )
            mockMvc.perform(put(putUrl()).contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isNotFound)
        }
    }
}

