package br.all.application.review.update

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.toDto
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.ReviewId
import br.all.domain.model.review.SystematicStudy
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class UpdateSystematicStudyServiceTest{
    @MockK
    private lateinit var systematicStudyRepository : SystematicStudyRepository
    private lateinit var sut : UpdateSystematicStudyService

    @BeforeEach
    fun setUp() {
        sut = UpdateSystematicStudyService(systematicStudyRepository)
    }

    @Test
    fun `Should only the title be updated`() {
        val systematicStudyId = UUID.randomUUID()
        val requestModel = UpdateSystematicStudyRequestModel("New title", null)
        val systematicStudy = SystematicStudy(ReviewId(systematicStudyId), "Old title",
            "Old description", ResearcherId(UUID.randomUUID()))
        val oldDto = systematicStudy.toDto()

        systematicStudy.rename("New title")
        val newDto = systematicStudy.toDto()

        every { systematicStudyRepository.findById(systematicStudyId) } returns oldDto
        every { systematicStudyRepository.create(newDto) } returns Unit

        val updateStudy = sut.update(systematicStudyId, requestModel)
        assertEquals("New title", updateStudy.title)
        assertEquals("Old description", updateStudy.description)
    }
}