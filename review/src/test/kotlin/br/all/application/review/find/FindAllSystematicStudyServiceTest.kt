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
class FindAllSystematicStudyServiceTest {
    @MockK
    private lateinit var systematicStudyRepository : SystematicStudyRepository
    private lateinit var sut : FindAllSystematicStudyService

    @BeforeEach
    fun setUp() {
        sut = FindAllSystematicStudyService(systematicStudyRepository)
    }

    @Test
    fun `Should find every systematic study`() {
        val listOfStudies = getDummyListOfSystematicStudies(5)
        every { systematicStudyRepository.findAll() } returns listOfStudies

        val responseModel = sut.findAll()
        assertEquals(5, responseModel.studies.size)
    }

    private fun getDummyListOfSystematicStudies(length: Int) : List<SystematicStudyDto> {
        val listOfStudies = mutableListOf<SystematicStudyDto>()

        repeat(length) {
            listOfStudies.add(SystematicStudyDto(UUID.randomUUID(), "Some title", "Some description",
                UUID.randomUUID(), emptySet()))
        }

        return listOfStudies
    }
}
