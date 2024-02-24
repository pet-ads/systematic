package br.all.search.controller

<<<<<<< HEAD
import br.all.domain.shared.utils.paragraph
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.search.MongoSearchSessionRepository
import com.ninjasquad.springmockk.clear
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
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
            val json = MockMultipartFile("request", factory.validPostRequest().toByteArray())

            mockMvc.perform(multipart(postUrl())
                .file(factory.bibfile())
                .file(json))
                .andExpect(status().isCreated);

//            mockMvc.perform(MockMvcRequestBuilders.multipart(postUrl())
//                .file(factory.bibfile())
//                .param("systematicStudyId", systematicStudyId.toString())
//                .param("source", "ACM")
//                .param("searchString", "LUCAS AND REVIEW")
//                .param("additionalInfo", "Loren ipsum"))
//                .andExpect(status().isOk)
//                .andExpect(MockMvcResultMatchers.jsonPath("$.systematicStudyId").value(systematicStudyId.toString()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.studyReviewId").exists())
//                .andReturn()
        }

    }
}
=======
class SearchSessionControllerTest {
}
>>>>>>> 69c5f383551801509fb6df2324fb49516a685399
