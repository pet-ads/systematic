package br.all.application.protocol.create

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.fromRequestModel
import br.all.application.protocol.repository.toDto
import br.all.application.protocol.util.FakeProtocolRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.model.protocol.Protocol
import br.all.domain.services.UuidGeneratorService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class CreateProtocolServiceImplTest {
    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService
    private lateinit var protocolRepository: ProtocolRepository
    private lateinit var sut: CreateProtocolServiceImpl

    @BeforeEach
    fun setUp() {
        protocolRepository = FakeProtocolRepository()
        sut = CreateProtocolServiceImpl(protocolRepository, systematicStudyRepository, uuidGeneratorService)
    }

    @Test
    fun `Should successfully create a new protocol`() {
        val protocolId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val requestModel = getProtocolRequestModel(protocolId, reviewId)
        val protocolDto = Protocol.fromRequestModel(protocolId, reviewId, requestModel).toDto()

        every { uuidGeneratorService.next() } returns protocolId
        every { systematicStudyRepository.existsById(reviewId) } returns true

        sut.create(reviewId, requestModel)

        val createdDto = protocolRepository.findById(protocolId)
        assertEquals(protocolDto, createdDto)
    }

    private fun getProtocolRequestModel(protocolId: UUID, reviewId: UUID): ProtocolRequestModel {
        return ProtocolRequestModel(
            goal = "Something",
            justification = "It is important",

            researchQuestions = setOf("What is the questions which its answer is 42?"),
            keywords = setOf("Keyword"),
            searchString = "String",
            informationSources = setOf("SomeSourceWithManyPhilophicalArticles"),
            sourcesSelectionCriteria = "I want so",

            searchMethod = "Reading philosophical articles",
            studiesLanguages = setOf("ENGLISH"),
            studyTypeDefinition = "Primary",

            selectionProcess = "Classify articles by criteria",
            selectionCriteria = setOf(
                "It has deep reflection about life" to "INCLUSION",
                "It does not talk about life" to "EXCLUSION",
            ),
            dataCollectionProcess = "Reading the articles and reflecting about them",
            analysisAndSynthesisProcess = "Analyse opinions on each article",

            population = null,
            control = null,
            intervention = null,
            outcome = null,
            context = null,
        )
    }
}
