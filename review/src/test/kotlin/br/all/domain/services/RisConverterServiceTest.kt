package br.all.domain.services

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.*
import br.all.domain.services.BibtexTestData.testInputs
import br.all.domain.services.RisTestData.testInput
import org.junit.jupiter.api.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RisConverterServiceTest {
    private lateinit var sut: RisConverterService
    private lateinit var idGeneratorService: IdGeneratorService

    @BeforeEach
    fun setup() {
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        idGeneratorService = FakeIdGeneratorService
        sut = RisConverterService(idGeneratorService)
    }

    @AfterEach
    fun teardown() {
        val fake = idGeneratorService as FakeIdGeneratorService
        fake.reset()
    }

        @Test
        fun `Should create a StudyReview list from multiple bibtex entries as input`() {
            val ris = testInput["multiple RIS entries"]!!
            val studyReviewList = sut.convertManyToStudyReview(SystematicStudyId(UUID.randomUUID()), ris)
            assertEquals(3, studyReviewList.size)
        }

}