package br.all.protocol.controller

import br.all.infrastructure.protocol.MongoProtocolRepository
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.protocol.shared.TestDataFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import br.all.review.shared.TestDataFactory as SystematicStudyTestDataFactory

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
class ProtocolControllerTest(
    @Autowired private val protocolRepository: MongoProtocolRepository,
    @Autowired private val systematicStudyRepository: MongoSystematicStudyRepository,
    @Autowired private val mockMvc: MockMvc,
) {
    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyDataFactory: SystematicStudyTestDataFactory

    @BeforeEach
    fun setUp()  {
        protocolRepository.deleteAll()
        factory = TestDataFactory()
        systematicStudyDataFactory = SystematicStudyTestDataFactory()
    }

    @AfterEach
    fun tearDown() {
        protocolRepository.deleteAll()
    }

    @Nested
    @DisplayName("When posting protocols")
    inner class WhenPostingProtocols {
        @BeforeEach
        fun setUp() {
            val (researcher, systematicStudyId) = factory
            val systematicStudy = systematicStudyDataFactory.createSystematicStudyDocument(
                id = systematicStudyId,
                collaborators = mutableSetOf(researcher),
            )
            systematicStudyRepository.save(systematicStudy)
        }

        @Test
        @Tag("ValidClasses")
        fun `should post a new protocol`() {
            val (researcher, systematicStudy) = factory
            val json = factory.validPostRequest()

            mockMvc.perform(post(postUrl()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.researcherId").value(researcher.toString()))
                .andExpect(jsonPath("$.systematicStudyId").value(systematicStudy.toString()))
                .andExpect(jsonPath("$._links").exists())
        }

        private fun postUrl(
            researcher: UUID = factory.researcher,
            systematicStudy: UUID = factory.protocol,
        ) = "/researcher/$researcher/systematic-study/$systematicStudy/protocol"
    }
}