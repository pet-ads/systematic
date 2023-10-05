package br.all.application.review.update

import br.all.application.review.repository.SystematicStudyDto
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
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class UpdateSystematicStudyServiceTest{
    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    private lateinit var systematicStudyId: UUID
    private lateinit var updatingSystematicStudy: SystematicStudy
    private lateinit var oldDto: SystematicStudyDto
    private lateinit var sut: UpdateSystematicStudyService

    @BeforeEach
    fun setUp() {
        sut = UpdateSystematicStudyService(systematicStudyRepository)

        systematicStudyId = UUID.randomUUID()
        updatingSystematicStudy = SystematicStudy(ReviewId(systematicStudyId), "Old title",
                            "Old description", ResearcherId(UUID.randomUUID()))
        oldDto = updatingSystematicStudy.toDto()
    }

    @Test
    fun `Should only the title be updated`() {
        val requestModel = UpdateSystematicStudyRequestModel("New title", null)

        updatingSystematicStudy.rename("New title")
        val newDto = updatingSystematicStudy.toDto()
        mockkRepositoryToSunnyDay(newDto)

        val updateStudy = sut.update(systematicStudyId, requestModel)
        assertEquals("New title", updateStudy.title)
        assertEquals("Old description", updateStudy.description)
    }

    @Test
    fun `Should only the description be updated`() {
        val requestModel = UpdateSystematicStudyRequestModel(null, "New description")

        updatingSystematicStudy.changeDescription("New description")
        val newDto = updatingSystematicStudy.toDto()
        mockkRepositoryToSunnyDay(newDto)

        val updatedStudy = sut.update(systematicStudyId, requestModel)
        assertEquals("Old title", updatedStudy.title)
        assertEquals("New description", updatedStudy.description)
    }

    @Test
    fun `Should both title and description be updated`() {
        val requestModel = UpdateSystematicStudyRequestModel("New title", "New description")

        updatingSystematicStudy.rename("New title")
        updatingSystematicStudy.changeDescription("New description")
        val newDto = updatingSystematicStudy.toDto()
        mockkRepositoryToSunnyDay(newDto)

        val updatedStudy = sut.update(systematicStudyId, requestModel)
        assertEquals("New title", updatedStudy.title)
        assertEquals("New description", updatedStudy.description)
    }

    @ParameterizedTest
    @CsvSource(",", "Old title,Old description")
    fun `Should the study keep the same`(title: String?, description: String?) {
        val requestModel = UpdateSystematicStudyRequestModel(title, description)

        updatingSystematicStudy.rename(title ?: updatingSystematicStudy.title)
        updatingSystematicStudy.changeDescription(description ?: updatingSystematicStudy.description)
        val newDto = updatingSystematicStudy.toDto()
        mockkRepositoryToSunnyDay(newDto)

        val updatedStudy = sut.update(systematicStudyId, requestModel)
        assertEquals("Old title", updatedStudy.title)
        assertEquals("Old description", updatedStudy.description)
    }

    @Test
    fun `Should throw NoSuchElementException for nonexistent systematic study`() {
        val requestModel = UpdateSystematicStudyRequestModel(null, null)
        every { systematicStudyRepository.findById(systematicStudyId) } returns null
        assertThrows<NoSuchElementException> { sut.update(systematicStudyId, requestModel) }
    }

    private fun mockkRepositoryToSunnyDay(newDto: SystematicStudyDto) {
        every { systematicStudyRepository.findById(systematicStudyId) } returns oldDto
        every { systematicStudyRepository.create(newDto) } returns Unit
    }
}