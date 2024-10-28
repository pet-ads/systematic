package br.all.domain.services

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.domain.model.study.*
import br.all.domain.services.RisTestData.testInput
import org.junit.jupiter.api.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RisConverterServiceTest {
    private lateinit var sut: RisConverterService
    private lateinit var idGeneratorService: IdGeneratorService

    @BeforeEach
    fun setup() {
        idGeneratorService = FakeIdGeneratorService
        sut = RisConverterService(idGeneratorService)
    }

    @AfterEach
    fun teardown() {
        val fake = idGeneratorService as FakeIdGeneratorService
        fake.reset()
    }

    @Nested
    inner class IndividualTests {
        @Test
        fun `Should create a StudyReview list from multiple ris entries`() {
            val ris = testInput["error ris"]!!
            val studyReviewList = sut.convertManyToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                ris
            )
            println(studyReviewList)
            assertEquals(studyReviewList.size, 10)
        }

        @Test
        fun `Should return correct study type`() {
            val ris = testInput["valid RIS entrie"]!!
            val art = StudyType.valueOf("ARTICLE")
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
            assertEquals(art, studyReview.studyType)
        }

        @Test
        fun `should return correct title`() {
            val ris = testInput["valid RIS entrie"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
            assertEquals(
                studyReview.title,
                "Sampling for Scalable Visual Analytics IEEE Computer Graphics and Applications"
            )
        }

        @Test
        fun `should return correct title from T1 key`() {
            val ris = testInput["T1 test"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
            assertEquals(
                studyReview.title,
                "Sampling for Scalable Visual Analytics IEEE Computer Graphics and Applications"
            )
        }

        @Test
        fun `should return correct publication year`() {
            val ris = testInput["valid RIS entrie"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
            assertEquals(studyReview.year, 2017)
        }

        @Test
        fun `should return correct publication year from Y1 key`() {
            val ris = testInput["Y1 test"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
            assertEquals(studyReview.year, 2017)
        }

        @Test
        fun `should return correct list of authors`() {
            val ris = testInput["valid RIS entrie"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
            assertEquals("B. C. Kwon, P. J. Haas", studyReview.authors)
        }

        @Test
        fun `should return correct list of authors from A1 keys`() {
            val ris = testInput["multiple A1"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
            assertEquals("B. C. Kwon, P. J. Haas", studyReview.authors)
        }

        @Test
        fun `should return the correct venue`() {
            val ris = testInput["valid RIS entrie"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
            assertEquals("IEEE Computer Graphics and Applications", studyReview.venue)
        }

        @Test
        fun `should return the correct abstract`() {
            val ris = testInput["valid RIS entrie"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
            assertEquals("Lorem Ipsum", studyReview.abstract)
        }

        @Test
        fun `should return the correct keywords`() {
            val ris = testInput["valid RIS entrie"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
            assertAll(
                { assertTrue("Temperature sensors" in studyReview.keywords) },
                { assertTrue("Data visualization" in studyReview.keywords) }
            )
        }

        @Test
        fun `should return correct doi`() {
            val ris = testInput["valid RIS entrie"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
            val expectedDoi = "https://doi.org/10.1109/MCG.2017.6"
            assertEquals(expectedDoi, studyReview.doi?.value)
        }
    }

    @Nested
    inner class ValidClasses {

        @Test
        fun `Should create a StudyReview list from multiple RIS entries as input`() {
            val ris = testInput["multiple RIS entries"]!!
            val studyReviewList = sut.convertManyToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                ris
            )
            assertEquals(3, studyReviewList.size)
        }

        @Test
        fun `should create article from valid input`() {
            val ris = testInput["valid RIS entrie"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
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

        @Test
        fun `should create inproceedings from valid input`() {
            val ris = testInput["valid inproceedings"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
            assertAll(
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.INPROCEEDINGS, studyReview.studyType) },
                { assertEquals("Muitos testes", studyReview.title) },
                { assertEquals(2017, studyReview.year) },
                { assertEquals("Gabriel, Erick", studyReview.authors) },
                { assertEquals("Meu Computador", studyReview.venue) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
                { assertTrue("Tes" in studyReview.keywords) },
                { assertTrue("Tes" in studyReview.keywords) },
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

        @Test
        fun `should create techreport from valid input`() {
            val ris = testInput["valid techreport"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )
            val expected = "Uma abordagem apoiada por linguagens específicas de domínio " +
                    "para a criação de linhas de produto de software embarcado"

            assertAll(
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.TECHREPORT, studyReview.studyType) },
                { assertEquals("Rafael Serapilha Durelli", studyReview.authors) },
                { assertEquals(expected, studyReview.title) },
                { assertEquals("Universidade Federal de São Carlos (UFSCar)", studyReview.venue) },
                { assertEquals(2011, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
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

        @Test
        fun `should create book from valid input`() {
            val ris = testInput["valid book"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )

            assertAll(
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.BOOK, studyReview.studyType) },
                { assertEquals("Len Bass, Paul Clements, Rick Kazman", studyReview.authors) },
                { assertEquals("Software Architecture in Practice", studyReview.title) },
                { assertEquals("Addison-Wesley", studyReview.venue) },
                { assertEquals(2012, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
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

        @Test
        fun `should create proceedings from valid input`() {
            val ris = testInput["valid proceedings"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )

            val expected = "Proceedings of the 17th International Conference on Computation " +
                    "and Natural Computation, Fontainebleau, France"

            assertAll(
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.PROCEEDINGS, studyReview.studyType) },
                { assertEquals("Susan Stepney", studyReview.authors) },
                { assertEquals(expected, studyReview.title) },
                { assertEquals("Springer", studyReview.venue) },
                { assertEquals(2018, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
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

        @Test
        fun `should create phdthesis from valid input`() {
            val ris = testInput["valid masterthesis"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )

            assertAll(
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.MASTERSTHESIS, studyReview.studyType) },
                { assertEquals("Rempel, Robert Charles", studyReview.authors) },
                { assertEquals("Relaxation Effects for Coupled Nuclear Spins", studyReview.title) },
                { assertEquals("Stanford University", studyReview.venue) },
                { assertEquals(1956, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
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

        @Test
        fun `should create inbook from valid input`() {
            val ris = testInput["valid inbook"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )

            assertAll(
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.INBOOK, studyReview.studyType) },
                { assertEquals("Rempel, Robert Charles", studyReview.authors) },
                { assertEquals("Relaxation Effects for Coupled Nuclear Spins", studyReview.title) },
                { assertEquals("Stanford University", studyReview.venue) },
                { assertEquals(1956, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
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

        @Test
        fun `should create booklet from valid input`() {
            val ris = testInput["valid booklet"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )

            assertAll(
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.BOOKLET, studyReview.studyType) },
                { assertEquals("Rempel, Robert Charles", studyReview.authors) },
                { assertEquals("Relaxation Effects for Coupled Nuclear Spins", studyReview.title) },
                { assertEquals("Stanford University", studyReview.venue) },
                { assertEquals(1956, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
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

        @Test
        fun `should create misc from valid input`() {
            val ris = testInput["valid misc"]!!
            val study = sut.convert(ris)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(UUID.randomUUID()),
                SearchSessionID(UUID.randomUUID()),
                study
            )

            assertAll(
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.MISC, studyReview.studyType) },
                { assertEquals("NASA", studyReview.authors) },
                { assertEquals("Pluto: The 'Other' Red Planet", studyReview.title) },
                { assertEquals("\\url{https://www.nasa.gov/nh/pluto-the-other-red-planet}", studyReview.venue) },
                { assertEquals(2015, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
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

    @Nested
    inner class InvalidClasses {
        @Test
        fun `convertManyToStudyReview should not accept a blank ris entry as input`() {
            assertThrows<IllegalArgumentException> {
                sut.convertManyToStudyReview(
                    SystematicStudyId(UUID.randomUUID()),
                    SearchSessionID(UUID.randomUUID()),
                    ""
                )
            }
        }

        @Test
        fun `convertToStudyReview should not accept a blank ris entry as input`() {
            assertThrows<IllegalArgumentException> {
                val study = sut.convert("")
                sut.convertToStudyReview(
                    SystematicStudyId(UUID.randomUUID()),
                    SearchSessionID(UUID.randomUUID()),
                    study
                )
            }
        }

        @Test
        fun `should throw IllegalArgumentException for unknown type entry`() {
            val ris = testInput["unknown ris"]!!
            assertThrows<IllegalArgumentException> {
                val study = sut.convert(ris)
                sut.convertToStudyReview(
                    SystematicStudyId(UUID.randomUUID()),
                    SearchSessionID(UUID.randomUUID()),
                    study
                )
            }
        }

        @Test
        fun `should throw IllegalArgumentException for invalid title entry`() {
            val ris = testInput["invalid title"].toString()
            assertThrows<IllegalArgumentException> {
                val study = sut.convert(ris)
                sut.convertToStudyReview(
                    SystematicStudyId(UUID.randomUUID()),
                    SearchSessionID(UUID.randomUUID()),
                    study
                )
            }
        }

        @Test
        fun `should throw IllegalArgumentException for invalid author entry`() {
            val ris = testInput["invalid authors"]!!
            assertThrows<IllegalArgumentException> {
                val study = sut.convert(ris)
                sut.convertToStudyReview(
                    SystematicStudyId(UUID.randomUUID()),
                    SearchSessionID(UUID.randomUUID()),
                    study
                )
            }
        }

        @Test
        fun `should throw IllegalArgumentException for invalid year entry`() {
            val ris = testInput["invalid year"]!!
            assertThrows<IllegalArgumentException> {
                val study = sut.convert(ris)
                sut.convertToStudyReview(
                    SystematicStudyId(UUID.randomUUID()),
                    SearchSessionID(UUID.randomUUID()),
                    study
                )
            }
        }

        @Test
        fun `should throw IllegalArgumentException for invalid venue entry`() {
            val ris = testInput["invalid venue"]!!
            assertThrows<IllegalArgumentException> {
                val study = sut.convert(ris)
                sut.convertToStudyReview(
                    SystematicStudyId(UUID.randomUUID()),
                    SearchSessionID(UUID.randomUUID()),
                    study
                )
            }
        }

        @Test
        fun `should throw IllegalArgumentException for invalid abstract entry`() {
            val ris = testInput["invalid abstract"]!!
            assertThrows<IllegalArgumentException> {
                val study = sut.convert(ris)
                sut.convertToStudyReview(
                    SystematicStudyId(UUID.randomUUID()),
                    SearchSessionID(UUID.randomUUID()),
                    study
                )
            }
        }

        @Test
        fun `should throw IllegalArgumentException for invalid doi`() {
            val ris = testInput["invalid doi"]!!
            assertThrows<IllegalArgumentException> {
                val study = sut.convert(ris)
                sut.convertToStudyReview(
                    SystematicStudyId(UUID.randomUUID()),
                    SearchSessionID(UUID.randomUUID()),
                    study
                )
            }
        }
    }
}
