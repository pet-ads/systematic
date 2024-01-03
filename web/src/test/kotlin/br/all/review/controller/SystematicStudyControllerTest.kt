package br.all.review.controller

import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.review.shared.DummyFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
class SystematicStudyControllerTest(
    @Autowired private val repository: MongoSystematicStudyRepository,
    @Autowired private val mockMvc: MockMvc,
) {
    private lateinit var dummyFactory: DummyFactory

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        dummyFactory = DummyFactory()
    }

    @AfterEach
    fun tearDown() = repository.deleteAll()

    private fun postUrl() = "/api/v1/researcher/${dummyFactory.researcherId}/systematic-study"

    @Nested
    @DisplayName("When posting a new Systematic Study")
    inner class WhenPostingANewSystematicStudy {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And being able to create it")
        inner class AndBeingAbleToCreateIt {
            @Test
            fun `Should create a valid systematic study`() {
                val json = dummyFactory.createValidPostRequest()
                mockMvc.perform(post(postUrl()).contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isCreated)
                    .andExpect(jsonPath("$.researcherId").value(dummyFactory.researcherId.toString()))
                    .andExpect(jsonPath("$.systematicStudyId").isString)
            }
        }
    }
}