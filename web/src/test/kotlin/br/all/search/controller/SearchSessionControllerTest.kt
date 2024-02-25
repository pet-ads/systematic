package br.all.search.controller

import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.search.MongoSearchSessionRepository
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class SearchSessionControllerTest(
    @Autowired val repository: MongoSearchSessionRepository,
    @Autowired val systematicStudyRepository: MongoSystematicStudyRepository,
    @Autowired val mockMvc: MockMvc,
) {

    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyId: UUID
    private lateinit var researcherId: UUID

    fun postUrl() = "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId/search-session"

    @BeforeEach
    fun setUp() {
        repository.deleteAll()

        factory = TestDataFactory()
        systematicStudyId = factory.systematicStudyId
        researcherId = factory.researcherId

        systematicStudyRepository.deleteAll()
        systematicStudyRepository.save(
            br.all.review.shared.TestDataFactory().createSystematicStudyDocument(
                id = systematicStudyId,
                owner = researcherId,
            )
        )
    }

    @AfterEach
    fun teardown() = repository.deleteAll()

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
                .andReturn()
        }
    }
}

