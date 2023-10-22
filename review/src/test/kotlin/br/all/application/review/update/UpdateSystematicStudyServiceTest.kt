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
    private var systematicStudyId = UUID.randomUUID()
    private lateinit var systematicStudyDto: SystematicStudyDto
    private lateinit var sut: UpdateSystematicStudyService

    @BeforeEach
    fun setUp() {
        systematicStudyRepository = FakeSystematicStudyRepository()
        sut = UpdateSystematicStudyService(systematicStudyRepository)

        systematicStudyDto = SystematicStudyDto(
            systematicStudyId,
            "Old title",
            "Old description",
            UUID.randomUUID(),
            emptySet()
        )
    }

    @Test
    fun `Should only the title be updated`() {
        val requestModel = UpdateSystematicStudyRequestModel("New title", null)

        systematicStudyRepository.create(systematicStudyDto)
        sut.update(systematicStudyId, requestModel)
        val updateStudy = systematicStudyRepository.findById(systematicStudyId)

        assertEquals("New title", updateStudy?.title)
        assertEquals("Old description", updateStudy?.description)
    }

    @Test
    fun `Should only the description be updated`() {
        val requestModel = UpdateSystematicStudyRequestModel(null, "New description")

        systematicStudyRepository.create(systematicStudyDto)
        sut.update(systematicStudyId, requestModel)
        val updatedStudy = systematicStudyRepository.findById(systematicStudyId)

        assertEquals("Old title", updatedStudy?.title)
        assertEquals("New description", updatedStudy?.description)
    }

    @Test
    fun `Should both title and description be updated`() {
        val requestModel = UpdateSystematicStudyRequestModel("New title", "New description")

        systematicStudyRepository.create(systematicStudyDto)
        sut.update(systematicStudyId, requestModel)
        val updatedStudy = systematicStudyRepository.findById(systematicStudyId)

        assertEquals("New title", updatedStudy?.title)
        assertEquals("New description", updatedStudy?.description)
    }

    @ParameterizedTest
    @CsvSource(",", "Old title,Old description")
    fun `Should the study keep the same`(title: String?, description: String?) {
        val requestModel = UpdateSystematicStudyRequestModel(title, description)

        systematicStudyRepository.create(systematicStudyDto)
        sut.update(systematicStudyId, requestModel)
        val updatedStudy = systematicStudyRepository.findById(systematicStudyId)

        assertEquals("Old title", updatedStudy?.title)
        assertEquals("Old description", updatedStudy?.description)
    }

    @Test
    fun `Should throw NoSuchElementException for nonexistent systematic study`() {
        val requestModel = UpdateSystematicStudyRequestModel(null, null)
        assertThrows<NoSuchElementException> { sut.update(systematicStudyId, requestModel) }
    }
}