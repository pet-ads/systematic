package br.all.search.controller

import br.all.application.search.find.service.FindAllSearchSessionsService
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.search.MongoSearchSessionRepository
import br.all.infrastructure.study.MongoStudyReviewRepository
import br.all.infrastructure.study.StudyReviewIdGeneratorService
import br.all.search.presenter.RestfulFindAllSearchSessionsPresenter
import org.junit.jupiter.api.*
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
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

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
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

            mockMvc.perform(multipart(postUrl()).file(factory.bibfile()).param("data", factory.validPostRequest(unauthorizedResearcherId)))
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
        fun `should find all the search sessions and return 200`(){
            val id1= UUID.randomUUID()
            val id2= UUID.randomUUID()
            val id3= UUID.randomUUID()
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
}

