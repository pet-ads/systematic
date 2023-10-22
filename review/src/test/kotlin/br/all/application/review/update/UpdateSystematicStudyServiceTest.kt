package br.all.application.review.update

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.toDto
import br.all.application.review.util.FakeSystematicStudyRepository
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.ReviewId
import br.all.domain.model.review.SystematicStudy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.*

class UpdateSystematicStudyServiceTest{
    private lateinit var systematicStudyRepository: SystematicStudyRepository
    private lateinit var systematicStudyId: UUID
    private lateinit var updatingSystematicStudy: SystematicStudy
    private lateinit var oldDto: SystematicStudyDto
    private lateinit var sut: UpdateSystematicStudyService

    @BeforeEach
    fun setUp() {
        systematicStudyRepository = FakeSystematicStudyRepository()
        sut = UpdateSystematicStudyService(systematicStudyRepository)
        systematicStudyId = UUID.randomUUID()
        updatingSystematicStudy = SystematicStudy(ReviewId(systematicStudyId), "Old title",
                            "Old description", ResearcherId(UUID.randomUUID()))
        oldDto = updatingSystematicStudy.toDto()
    }

    @Test
    fun `Should only the title be updated`() {
        val requestModel = UpdateSystematicStudyRequestModel("New title", null)

        updatingSystematicStudy.title = "New title"
        val newDto = updatingSystematicStudy.toDto()
        systematicStudyRepository.create(oldDto)

        sut.update(systematicStudyId, requestModel)
        val updateStudy = systematicStudyRepository.findById(systematicStudyId)

        assertEquals("New title", updateStudy?.title)
        assertEquals("Old description", updateStudy?.description)
    }

    @Test
    fun `Should only the description be updated`() {
        val requestModel = UpdateSystematicStudyRequestModel(null, "New description")

        updatingSystematicStudy.description = "New description"
        val newDto = updatingSystematicStudy.toDto()
        systematicStudyRepository.create(oldDto)

        sut.update(systematicStudyId, requestModel)
        val updatedStudy = systematicStudyRepository.findById(systematicStudyId)

        assertEquals("Old title", updatedStudy?.title)
        assertEquals("New description", updatedStudy?.description)
    }

    @Test
    fun `Should both title and description be updated`() {
        val requestModel = UpdateSystematicStudyRequestModel("New title", "New description")

        updatingSystematicStudy.title = "New title"
        updatingSystematicStudy.description = "New description"
        val newDto = updatingSystematicStudy.toDto()
        systematicStudyRepository.create(oldDto)

        sut.update(systematicStudyId, requestModel)
        val updatedStudy = systematicStudyRepository.findById(systematicStudyId)

        assertEquals("New title", updatedStudy?.title)
        assertEquals("New description", updatedStudy?.description)
    }

    @ParameterizedTest
    @CsvSource(",", "Old title,Old description")
    fun `Should the study keep the same`(title: String?, description: String?) {
        val requestModel = UpdateSystematicStudyRequestModel(title, description)

        updatingSystematicStudy.title = title ?: updatingSystematicStudy.title
        updatingSystematicStudy.description = description ?: updatingSystematicStudy.description
        val newDto = updatingSystematicStudy.toDto()
        systematicStudyRepository.create(oldDto)

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