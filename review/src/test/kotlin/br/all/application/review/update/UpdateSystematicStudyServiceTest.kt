package br.all.application.review.update

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.util.FakeSystematicStudyRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.*

class UpdateSystematicStudyServiceTest{
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    private lateinit var systematicStudyDto: SystematicStudyDto
    private lateinit var sut: UpdateSystematicStudyService

    @BeforeEach
    fun setUp() {
        systematicStudyRepository = FakeSystematicStudyRepository()
        sut = UpdateSystematicStudyService(systematicStudyRepository)

        systematicStudyDto = SystematicStudyDto(
            UUID.randomUUID(),
            "Old title",
            "Old description",
            UUID.randomUUID(),
            emptySet()
        )
    }

    @Test
    fun `Should only the title be updated`() {
        val requestModel = UpdateSystematicStudyRequestModel("New title", null)
        val updatedStudy = executeUpdateInSunnyDay(requestModel)

        assertEquals("New title", updatedStudy?.title)
        assertEquals("Old description", updatedStudy?.description)
    }

    @Test
    fun `Should only the description be updated`() {
        val requestModel = UpdateSystematicStudyRequestModel(null, "New description")
        val updatedStudy = executeUpdateInSunnyDay(requestModel)

        assertEquals("Old title", updatedStudy?.title)
        assertEquals("New description", updatedStudy?.description)
    }

    @Test
    fun `Should both title and description be updated`() {
        val requestModel = UpdateSystematicStudyRequestModel("New title", "New description")
        val updatedStudy = executeUpdateInSunnyDay(requestModel)

        assertEquals("New title", updatedStudy?.title)
        assertEquals("New description", updatedStudy?.description)
    }

    @ParameterizedTest
    @CsvSource(",", "Old title,Old description")
    fun `Should the study keep the same`(title: String?, description: String?) {
        val requestModel = UpdateSystematicStudyRequestModel(title, description)
        val updatedStudy = executeUpdateInSunnyDay(requestModel)

        assertEquals("Old title", updatedStudy?.title)
        assertEquals("Old description", updatedStudy?.description)
    }

    private fun executeUpdateInSunnyDay(requestModel: UpdateSystematicStudyRequestModel): SystematicStudyDto? {
        systematicStudyRepository.create(systematicStudyDto)
        sut.update(systematicStudyDto.id, requestModel)
        return systematicStudyRepository.findById(systematicStudyDto.id)
    }

    @Test
    fun `Should throw NoSuchElementException for nonexistent systematic study`() {
        val requestModel = UpdateSystematicStudyRequestModel(null, null)
        assertThrows<NoSuchElementException> { sut.update(systematicStudyDto.id, requestModel) }
    }
}