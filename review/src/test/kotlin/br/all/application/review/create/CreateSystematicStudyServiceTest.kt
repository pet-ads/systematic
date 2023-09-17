package br.all.application.review.create

import br.all.application.researcher.repository.ResearcherRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.domain.model.review.SystematicStudy
import br.all.domain.services.UuidGeneratorService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class CreateSystematicStudyServiceTest {
    @MockK
    private lateinit var systematicStudyRepository : SystematicStudyRepository
    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService
    @MockK
    private lateinit var researcherRepository : ResearcherRepository
    private lateinit var createSystematicStudyService : CreateSystematicStudyService

    @BeforeEach
    fun setUp() {
        createSystematicStudyService = CreateSystematicStudyService(systematicStudyRepository, researcherRepository,
                                            uuidGeneratorService)
    }

    @Test
    fun `Should create an systematic study`() {
        val researcherId = UUID.randomUUID()
        val requestModel = SystematicStudyRequestModel("Some title", "Some description",
                                setOf(researcherId))
        val id = UUID.randomUUID()
        val dto = SystematicStudy.fromRequestModel(id, requestModel).toDto()

        every { uuidGeneratorService.next() } returns id
        every { researcherRepository.existsById(researcherId) } returns true
        every { systematicStudyRepository.create(dto) } returns Unit
        every { systematicStudyRepository.findById(id) } returns Optional.of(dto)

        val systematicStudy = createSystematicStudyService.create(requestModel)
        assertEquals(dto, systematicStudy)
    }

    @Test
    fun `Should throw IllegalArgumentException for nonexistent researcher`() {
        val nonExistentResearcherId = UUID.randomUUID()
        val requestModel = SystematicStudyRequestModel("Some title", "Some description",
                                setOf(nonExistentResearcherId))
        val id = UUID.randomUUID()
        val dto = SystematicStudy.fromRequestModel(id, requestModel).toDto()

        every { uuidGeneratorService.next() } returns id
        every { researcherRepository.existsById(nonExistentResearcherId) } returns false
        every { systematicStudyRepository.create(dto) } returns Unit
        every { systematicStudyRepository.findById(id) } returns Optional.of(dto)

        assertThrows<IllegalArgumentException> { createSystematicStudyService.create(requestModel) }
    }
}