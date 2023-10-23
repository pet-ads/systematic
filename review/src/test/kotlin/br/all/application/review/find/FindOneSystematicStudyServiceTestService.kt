package br.all.application.review.find

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class FindOneSystematicStudyServiceTestService {
    @MockK
    private lateinit var systematicStudyRepository : SystematicStudyRepository
    private lateinit var sut : FindOneSystematicStudyService

    @BeforeEach
    fun setUp() {
        sut = FindOneSystematicStudyService(systematicStudyRepository)
    }

    @Test
    fun `Should find a single systematic study`() {
        val studyId = UUID.randomUUID()
        val systematicStudyDto = SystematicStudyDto(studyId, "Some title", "Some description",
            UUID.randomUUID(), emptySet())

        every { systematicStudyRepository.findById(studyId) } returns systematicStudyDto

        val responseModel = sut.findById(studyId)
        assertEquals(systematicStudyDto, responseModel)
    }

    @Test
    fun `Should not find any systematic study`() {
        val studyId = mockkRepositoryToFindNothing()

        val responseModel = sut.findById(studyId)
        assertEquals(null, responseModel)
    }

    private fun mockkRepositoryToFindNothing(): UUID {
        val studyId = UUID.randomUUID()
        every { systematicStudyRepository.findById(studyId) } returns null
        return studyId
    }

    @Test
    fun `Should systematic study exist`() {
        val studyId = UUID.randomUUID()
        val systematicStudyDto = SystematicStudyDto(studyId, "Some title", "Some description",
            UUID.randomUUID(), emptySet())

        every { systematicStudyRepository.findById(studyId) } returns systematicStudyDto
        assertTrue { sut.existById(studyId) }
    }

    @Test
    fun `Should systematic study not exist`() {
        val studyId = mockkRepositoryToFindNothing()
        assertFalse { sut.existById(studyId) }
    }
}
