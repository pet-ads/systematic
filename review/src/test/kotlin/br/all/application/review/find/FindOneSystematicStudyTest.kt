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

@ExtendWith(MockKExtension::class)
class FindOneSystematicStudyTest {
    @MockK
    private lateinit var systematicStudyRepository : SystematicStudyRepository
    private lateinit var sut : FindOneSystematicStudy

    @BeforeEach
    fun setUp() {
        sut = FindOneSystematicStudy(systematicStudyRepository)
    }

    @Test
    fun `Should find a single systematic study`() {
        val studyId = UUID.randomUUID()
        val systematicStudyDto = SystematicStudyDto(studyId, "Some title", "Some description",
                                        UUID.randomUUID(), emptySet())

        every { systematicStudyRepository.findById(studyId) } returns systematicStudyDto

        val responseModel = sut.findById(studyId)
        assertEquals(1, responseModel.studies.size)
    }

    @Test
    fun `Should not find any systematic study`() {
        val studyId = UUID.randomUUID()
        every { systematicStudyRepository.findById(studyId) } returns null

        val responseModel = sut.findById(studyId)
        assertEquals(0, responseModel.studies.size)
    }
}
