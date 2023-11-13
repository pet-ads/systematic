package br.all.application.protocol.find

import br.all.application.protocol.repository.ProtocolDto
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.model.protocol.Criteria
import br.all.domain.shared.utils.Language
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class FindOneProtocolServiceImplTest {
    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK
    private lateinit var protocolRepository: ProtocolRepository
    private lateinit var sut: FindOneProtocolServiceImpl

    @BeforeEach
    fun setUp() {
        sut = FindOneProtocolServiceImpl(protocolRepository, systematicStudyRepository)
    }

    @Test
    fun `Should find one existent protocol by its id`() {
        val protocolId = UUID.randomUUID()
        val dto = getDummyProtocolDto(protocolId)

        every { protocolRepository.findById(protocolId) } returns dto

        assertEquals(dto, sut.findById(protocolId))
    }

    @Test
    fun `Should not find a protocol by its id if it does not exist`() {
        val protocolId = UUID.randomUUID()
        every { protocolRepository.findById(protocolId) } returns null
        assertEquals(null, sut.findById(protocolId))
    }

    private fun getDummyProtocolDto(protocolId: UUID) = ProtocolDto(
        id = protocolId,
        reviewId = UUID.randomUUID(),

        goal = "Some goal",
        justification = "It is important",

        researchQuestions = setOf("What is the questions which its answer is 42?"),
        keywords = setOf("Keyword"),
        searchString = "String",
        informationSources = setOf("SomeSourceWithManyPhilosophicalArticles"),
        sourcesSelectionCriteria = "I want so",

        searchMethod = "Reading philosophical articles",
        studiesLanguages = setOf(Language.LangType.ENGLISH),
        studyTypeDefinition = "Primaries",

        selectionProcess = "Classify articles by criteria",
        selectionCriteria = setOf(
            "It has deep reflection about life" to Criteria.CriteriaType.INCLUSION,
            "It does not talk about life" to Criteria.CriteriaType.EXCLUSION,
        ),

        dataCollectionProcess = "Read the articles and reflect about them",
        analysisAndSynthesisProcess = "Analyse opinions on each article",

        extractionQuestions = emptySet(),
        robQuestions = emptySet(),

        picoc = null,
    )
}
