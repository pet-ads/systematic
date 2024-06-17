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
    @Nested
    inner class IndividualTests() {
        @Test
        fun `Should create a StudyReview list from multiple bibtex entries as input`() {
            val ris = testInput["multiple RIS entries"]!!
            val studyReviewList = sut.convertManyToStudyReview(SystematicStudyId(UUID.randomUUID()), ris)
            assertEquals(3, studyReviewList.size)
        }

        @Test
        fun `Should return correct study type`() {
            val ris = testInput["valid RIS entrie"]!!
            val art = StudyType.valueOf("ARTICLE")
            val study = sut.extractStudyType(ris)
            assertEquals(art, study)
        }

        @Test
        fun `should return correct title`() {
            val ris = testInput["valid RIS entrie"]!!
            val studyReview = sut.convertToStudyReview(SystematicStudyId(UUID.randomUUID()), ris)
            assertEquals(
                studyReview.title,
                "Sampling for Scalable Visual Analytics IEEE Computer Graphics and Applications"
            )
        }

        @Test
        fun `should return correct publication year`() {
            val ris = testInput["valid RIS entrie"]!!
            val studyReview = sut.convertToStudyReview(SystematicStudyId(UUID.randomUUID()), ris)
            assertEquals(studyReview.year, 2017)
        }

        @Test
        fun `should return correct list of authors`() {
            val ris = testInput["valid RIS entrie"]!!
            val studyReview = sut.convertToStudyReview(SystematicStudyId(UUID.randomUUID()), ris)
            assertEquals("B. C. Kwon, P. J. Haas", studyReview.authors)
        }

        @Test
        fun `should return the correct venue`() {
            val ris = testInput["valid RIS entrie"]!!
            val studyReview = sut.convertToStudyReview(SystematicStudyId(UUID.randomUUID()), ris)
            assertEquals("IEEE Computer Graphics and Applications", studyReview.venue)
        }

        @Test
        fun `should return the correct abstract`() {
            val ris = testInput["valid RIS entrie"]!!
            val studyReview = sut.convertToStudyReview(SystematicStudyId(UUID.randomUUID()), ris)
            assertEquals("Lorem Ipsum", studyReview.abstract)
        }

        @Test
        fun `should return the correct keywords`() {
            val ris = testInput["valid RIS entrie"]!!
            val studyReview = sut.convertToStudyReview(SystematicStudyId(UUID.randomUUID()), ris)
            assertAll(
                { assertTrue("Temperature sensors" in studyReview.keywords) },
                { assertTrue("Data visualization" in studyReview.keywords) }
            )
        }

        @Test
        fun `should return correct doi`() {
            val ris = testInput["valid RIS entrie"]!!
            val studyReview = sut.convertToStudyReview(SystematicStudyId(UUID.randomUUID()), ris)
            val expectedDoi = "https://doi.org/10.1109/MCG.2017.6"
            assertEquals(expectedDoi, studyReview.doi?.value)
        }

        @Test
        fun `should return list of ris entries`() {
            val ris = testInput["multiple RIS entries"]!!
            val study: List<Study> = sut.convertMany(ris)
            assertEquals(3, study.size)
        }
}

    @Nested
    inner class ValidClasses() {
        @Test
        fun `should create article from valid input`() {
            val ris = testInput["valid RIS entrie"]!!
            val studyReview = sut.convertToStudyReview(SystematicStudyId(UUID.randomUUID()), ris)
            assertAll(
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.ARTICLE, studyReview.studyType) },
                {
                    assertEquals(
                        "Sampling for Scalable Visual Analytics IEEE Computer Graphics and Applications",
                        studyReview.title
                    )
                },
                { assertEquals(2017, studyReview.year) },
                { assertEquals("B. C. Kwon, P. J. Haas", studyReview.authors) },
                { assertEquals("IEEE Computer Graphics and Applications", studyReview.venue) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
                { assertTrue("Temperature sensors" in studyReview.keywords) },
                { assertTrue("Data visualization" in studyReview.keywords) },
                { assertEquals(0, studyReview.references.size) },
                { assertEquals("https://doi.org/10.1109/MCG.2017.6", studyReview.doi?.value) },
                { assertEquals(1, studyReview.searchSources.size) },
                { assertTrue(studyReview.criteria.isEmpty()) },
                { assertTrue(studyReview.formAnswers.isEmpty()) },
                { assertTrue(studyReview.robAnswers.isEmpty()) },
                { assertEquals("", studyReview.comments) },
                { assertEquals(ReadingPriority.LOW, studyReview.readingPriority) },
                { assertEquals(SelectionStatus.UNCLASSIFIED, studyReview.selectionStatus) },
                { assertEquals(ExtractionStatus.UNCLASSIFIED, studyReview.extractionStatus) }
            )
        }
    }

}