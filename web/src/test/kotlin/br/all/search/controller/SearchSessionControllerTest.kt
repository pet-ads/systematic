package br.all.search.controller

import br.all.infrastructure.protocol.MongoProtocolRepository
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.search.MongoSearchSessionRepository
import br.all.infrastructure.shared.toNullable
import br.all.infrastructure.study.MongoStudyReviewRepository
import br.all.infrastructure.study.StudyReviewIdGeneratorService
import br.all.search.shared.TestDataFactory
import br.all.security.service.ApplicationUser
import br.all.shared.TestHelperService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
@Tag("ControllerTest")
class SearchSessionControllerTest(
    @Autowired val repository: MongoSearchSessionRepository,
    @Autowired val systematicStudyRepository: MongoSystematicStudyRepository,
    @Autowired val studyReviewRepository: MongoStudyReviewRepository,
    @Autowired val protocolRepository: MongoProtocolRepository,
    @Autowired val idService: StudyReviewIdGeneratorService,
    @Autowired private val testHelperService: TestHelperService,
    @Autowired val mockMvc: MockMvc,
) {

    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyId: UUID
    private lateinit var user: ApplicationUser
    private lateinit var unauthorizedUser: ApplicationUser


    fun postUrl() = "/api/v1/systematic-study/$systematicStudyId/search-session"
    fun patchUrl(sessionId: UUID) = "/api/v1/systematic-study/$systematicStudyId/patch-search-session/${sessionId}"
    fun findUrl(sessionId: UUID) =
        "/api/v1/systematic-study/$systematicStudyId/search-session/${sessionId}"
    fun findAllUrl() = "/api/v1/systematic-study/$systematicStudyId/search-session"
    fun findBySourceUrl(source: String) =
        "/api/v1/systematic-study/$systematicStudyId/search-session-source/${source}"

    fun putUrl(systematicStudyId: UUID = factory.systematicStudyId, searchSessionId: UUID = factory.sessionId) = "/api/v1/systematic-study/$systematicStudyId/search-session/$searchSessionId"


    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        repository.deleteAll()
        systematicStudyId = factory.systematicStudyId

        user = testHelperService.createApplicationUser()
        unauthorizedUser = testHelperService.createUnauthorizedApplicationUser()

        systematicStudyRepository.save(
            br.all.review.shared.TestDataFactory().createSystematicStudyDocument(
                id = systematicStudyId,
                owner = user.id,
            )
        )
    }

    @AfterEach
    fun teardown() {
        repository.deleteAll()
        systematicStudyRepository.deleteAll()
        protocolRepository.deleteAll()
        studyReviewRepository.deleteAll()
        testHelperService.deleteApplicationUser(user.id)
        idService.reset()
    }

    @Nested
    @DisplayName("When creating a search session")
    inner class CreateTests {
        @Test
        fun `should create search session and return 201`() {
            val source = "testSource"
            protocolRepository.save(factory.createProtocolDocument(source))

            mockMvc.perform(multipart(postUrl())
                .file(factory.bibfile()).param("data", factory.validPostRequest(source = source))
                .with(SecurityMockMvcRequestPostProcessors.user(user))
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$._links").exists())
                .andReturn()
        }

        @Test
        fun `should return 400 when BibTeX format is invalid`() {

            mockMvc.perform(
                multipart(postUrl())
                    .file("bibFile", factory.invalidBibFile())
                    .param("data", factory.validPostRequest())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
            )
                .andExpect(status().isBadRequest)
                .andReturn()
        }

        @Test
        fun `should return 403 when user is not authorized`() {

            testHelperService.testForUnauthorizedUser(mockMvc,
                multipart(postUrl())
                    .file(factory.bibfile())
                    .param("data", factory.validPostRequest())
            )
        }

        @Test
        fun `should return 403 when user is not authenticated`() {

            testHelperService.testForUnauthenticatedUser(mockMvc,
                multipart(postUrl())
                    .file(factory.bibfile())
                    .param("data", factory.validPostRequest()),
            )
        }
    }

    @Nested
    @DisplayName("When finding a search session")
    inner class FindTests {
        @Test
        fun `should find the search session and return 200`() {

            val searchSession = factory.searchSessionDocument(factory.sessionId, systematicStudyId)
            repository.insert(searchSession)

            mockMvc.perform(get(findUrl(searchSession.id))
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(searchSession.id.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        @Test
        fun `should return 403 when user is not authorized`() {

            val searchSession = factory.searchSessionDocument(factory.sessionId, systematicStudyId)
            repository.insert(searchSession)

            testHelperService.testForUnauthorizedUser(
                mockMvc,
                get(findUrl(searchSession.id))
                    .with(SecurityMockMvcRequestPostProcessors.user(unauthorizedUser))
            )
        }

        @Test
        fun `should return 403 when user is not authenticated`() {

            val searchSession = factory.searchSessionDocument(factory.sessionId, systematicStudyId)
            repository.insert(searchSession)

            testHelperService.testForUnauthenticatedUser(mockMvc, get(findUrl(searchSession.id)),
            )
        }

        @Test
        fun `should return 404 if don't find the search session`() {
            mockMvc.perform(
                get(findUrl(factory.nonExistentSessionId))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound)
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

            mockMvc.perform(
                get(findAllUrl())
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.size").value(2))
        }

        @Test
        fun `should return 404 if no search session is found`() {
            val id1 = UUID.randomUUID()
            mockMvc.perform(
                get(findUrl(id1))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should find all search sessions by source and return 200`(){
            val id1 = UUID.randomUUID()
            val id2 = UUID.randomUUID()
            val id3 = UUID.randomUUID()

            repository.insert(factory.searchSessionDocument(id1, systematicStudyId))
            repository.insert(factory.searchSessionDocument(id2, systematicStudyId))
            repository.insert(factory.searchSessionDocument(id3, systematicStudyId, source = "WrongSource"))

            mockMvc.perform(get(findBySourceUrl("Source"))
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
                .andExpect(jsonPath("$.size").value(2))
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
                source = "Old Search Source"
            )
            repository.insert(original)

            val request = factory.createValidPutRequest(searchString, additionalInfo, searchSource)
            mockMvc.perform(put(putUrl())
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.systematicStudyId").value(factory.systematicStudyId.toString()))
                .andExpect(jsonPath("$.searchSessionID").value(factory.sessionId.toString()))
                .andExpect(jsonPath("$._links").exists())

                assertNotEquals(original, repository.findById(factory.sessionId).toNullable())
        }

        @Test
        fun `should nothing be updated if nothing is provided`(){
            val document = factory.searchSessionDocument()
            repository.insert(document)

            val request = "{}"
            mockMvc.perform(put(putUrl())
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk)
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
            mockMvc.perform(put(putUrl())
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should return 403 if the user is unauthorized`(){
            val request = factory.createValidPutRequest(
                "New Search String", "New Additional Info", "New SearchSource"
            )
            testHelperService.testForUnauthorizedUser(
                mockMvc,
                put(putUrl())
                    .content(request)
                    .with(SecurityMockMvcRequestPostProcessors.user(unauthorizedUser))
            )
        }

        @Test
        fun `should not allow unauthenticated users to update a search session`(){
            val request = factory.createValidPutRequest(
                "New Search String", "New Additional Info", "New SearchSource"
            )
            testHelperService.testForUnauthenticatedUser(
                mockMvc,
                put(putUrl())
                    .content(request)
            )
        }
    }

//    @Nested
//    @DisplayName("When patching search session")
//    inner class WhenPatchingSearchSession {
//        @Test
//        fun `should patch the search session and return 200`() {
          // TODO() FIX THIS TEST
////            val request = factory.validPatchRequest()
////            val searchSession = factory.searchSessionDocument(factory.sessionId, systematicStudyId)
////            repository.insert(searchSession)
////
////            mockMvc.perform(
////                multipart(patchUrl(searchSession.id))
////                    .file(factory.bibfile())
////                    .param("data", request)
////                    .param("_method", "PATCH")
////                    .with(SecurityMockMvcRequestPostProcessors.user(user))
////            )
////                .andExpect(status().isOk)
//        }
//    }


    @Nested
    @DisplayName("When deleting a search session")
    inner class DeleteTests {

        @Test
        fun `should delete the search session and return 204`() {
            val searchSession = factory.searchSessionDocument(factory.sessionId, systematicStudyId)
            repository.insert(searchSession)

            mockMvc.perform(
                delete(findUrl(searchSession.id))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNoContent)
        }

        @Test
        fun `should return 404 when search session does not exist`() {
            val nonExistentSessionId = UUID.randomUUID()
            mockMvc.perform(
                delete(findUrl(nonExistentSessionId))
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should return 403 when user is not authorized`() {
            val searchSession = factory.searchSessionDocument(factory.sessionId, systematicStudyId)
            repository.insert(searchSession)

            testHelperService.testForUnauthorizedUser(
                mockMvc,
                delete(findUrl(searchSession.id))
                    .with(SecurityMockMvcRequestPostProcessors.user(unauthorizedUser))
            )
        }

        @Test
        fun `should return 403 when user is not authenticated`() {
            val searchSession = factory.searchSessionDocument(factory.sessionId, systematicStudyId)
            repository.insert(searchSession)

            testHelperService.testForUnauthenticatedUser(
                mockMvc,
                delete(findUrl(searchSession.id))
            )
        }
    }


}

