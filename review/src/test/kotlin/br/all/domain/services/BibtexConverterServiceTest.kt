package br.all.domain.services

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.domain.model.study.*
import br.all.domain.shared.exception.bibtex.BibtexParseException
import org.junit.jupiter.api.*
import java.util.UUID.randomUUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Tag("UnitTest")
@Tag("ServiceTest")
class BibtexConverterServiceTest {

    private lateinit var sut: BibtexConverterService
    private lateinit var idGeneratorService: IdGeneratorService

    @BeforeEach
    fun setup() {
        idGeneratorService = FakeIdGeneratorService
        sut = BibtexConverterService(idGeneratorService)
    }

    @AfterEach
    fun teardown() {
        val fake = idGeneratorService as FakeIdGeneratorService
        fake.reset(randomUUID())
    }

    @Nested
    inner class ValidClasses {

        @Test
        fun `Should create a StudyReview list from multiple bibtex entries as input`() {
            val bibtex = BibtexTestData.testInputs["multiple bibtex entries"]!!
            val studyReviewList = sut.convertManyToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                bibtex,
                source = mutableSetOf("Compendex")
            )
            assertEquals(7, studyReviewList.first.size)
        }

        @Test
        fun `should create article from valid input`() {
            val bibtex = BibtexTestData.testInputs["valid article"]!!
            val study = sut.convert(bibtex)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                study,
                source = mutableSetOf("Compendex")
            )

            assertAll(
                "bibtex",
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.ARTICLE, studyReview.studyType) },
                { assertEquals("Non-cooperative Games", studyReview.title) },
                { assertEquals(1951, studyReview.year) },
                { assertEquals("Nash, John", studyReview.authors) },
                { assertEquals("Annals of Mathematics", studyReview.venue) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
                { assertTrue("keyword1" in studyReview.keywords) },
                { assertTrue("keyword2" in studyReview.keywords) },
                { assertEquals(2, studyReview.references.size) },
                { assertEquals("https://doi.org/10.1234/doi", studyReview.doi?.value) },
                { assertEquals(1, studyReview.searchSources.size) },
                { assertTrue(studyReview.selectionCriteria.isEmpty()) },
                { assertTrue(studyReview.extractionCriteria.isEmpty()) },
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
            val bibtex = BibtexTestData.testInputs["valid inproceedings"]!!
            val study = sut.convert(bibtex)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                study,
                source = mutableSetOf("Compendex")
            )
            assertAll(
                "bibtex",
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.INPROCEEDINGS, studyReview.studyType) },
                { assertEquals("Onofre {Trindade Júnior}", studyReview.authors) },
                { assertEquals("{Using SOA in Critical-Embedded Systems}", studyReview.title) },
                { assertEquals("Proceedings of the 4${'$'}^{th}${'$'}  IEEE (CPSCom'11)", studyReview.venue) },
                { assertEquals(2011, studyReview.year) },
                { assertEquals("Lorem ipsum", studyReview.abstract) },
                { assertEquals(listOf("ref3", "ref4"), studyReview.references) },
                { assertEquals(Doi("https://doi.org/10.1021/ci025584y"), studyReview.doi) },
                { assertEquals(1, studyReview.searchSources.size) },
                { assertTrue(studyReview.selectionCriteria.isEmpty()) },
                { assertTrue(studyReview.extractionCriteria.isEmpty()) },
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
            val bibtex = BibtexTestData.testInputs["valid techreport"]!!
            val study = sut.convert(bibtex)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                study,
                source = mutableSetOf("Compendex")
            )
            val expected = "Uma abordagem apoiada por linguagens específicas de domínio para a criação de linhas de produto de software embarcado"

            assertAll(
                "bibtex",
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.TECHREPORT, studyReview.studyType) },
                { assertEquals("Rafael Serapilha Durelli", studyReview.authors) },
                { assertEquals(expected, studyReview.title) },
                { assertEquals("Universidade Federal de São Carlos (UFSCar)", studyReview.venue) },
                { assertEquals(2011, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
                { assertEquals(1, studyReview.searchSources.size) },
                { assertTrue(studyReview.selectionCriteria.isEmpty()) },
                { assertTrue(studyReview.extractionCriteria.isEmpty()) },
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
            val bibtex = BibtexTestData.testInputs["valid book"]!!
            val study = sut.convert(bibtex)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                study,
                source = mutableSetOf("Compendex")
            )
            assertAll(
                "bibtex",
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.BOOK, studyReview.studyType) },
                { assertEquals("Len Bass and Paul Clements and Rick Kazman", studyReview.authors) },
                { assertEquals("Software Architecture in Practice", studyReview.title) },
                { assertEquals("Addison-Wesley", studyReview.venue) },
                { assertEquals(2012, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
                { assertEquals(1, studyReview.searchSources.size) },
                { assertTrue(studyReview.selectionCriteria.isEmpty()) },
                { assertTrue(studyReview.extractionCriteria.isEmpty()) },
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
            val bibtex = BibtexTestData.testInputs["valid proceedings"]!!
            val study = sut.convert(bibtex)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                study,
                source = mutableSetOf("Compendex")
            )
            val expected = "Proceedings of the 17th International Conference on Computation and Natural Computation, Fontainebleau, France"

            assertAll(
                "bibtex",
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.PROCEEDINGS, studyReview.studyType) },
                { assertEquals("Susan Stepney and Sergey Verlan", studyReview.authors) },
                { assertEquals(expected, studyReview.title) },
                { assertEquals("Springer", studyReview.venue) },
                { assertEquals(2018, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
                { assertEquals(1, studyReview.searchSources.size) },
                { assertTrue(studyReview.selectionCriteria.isEmpty()) },
                { assertTrue(studyReview.extractionCriteria.isEmpty()) },
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
            val bibtex = BibtexTestData.testInputs["valid phdthesis"]!!
            val study = sut.convert(bibtex)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                study,
                source = mutableSetOf("Compendex")
            )
            assertAll(
                "bibtex",
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.PHDTHESIS, studyReview.studyType) },
                { assertEquals("Rempel, Robert Charles", studyReview.authors) },
                { assertEquals("Relaxation Effects for Coupled Nuclear Spins", studyReview.title) },
                { assertEquals("Stanford University", studyReview.venue) },
                { assertEquals(1956, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
                { assertEquals(1, studyReview.searchSources.size) },
                { assertTrue(studyReview.selectionCriteria.isEmpty()) },
                { assertTrue(studyReview.extractionCriteria.isEmpty()) },
                { assertTrue(studyReview.formAnswers.isEmpty()) },
                { assertTrue(studyReview.robAnswers.isEmpty()) },
                { assertEquals("", studyReview.comments) },
                { assertEquals(ReadingPriority.LOW, studyReview.readingPriority) },
                { assertEquals(SelectionStatus.UNCLASSIFIED, studyReview.selectionStatus) },
                { assertEquals(ExtractionStatus.UNCLASSIFIED, studyReview.extractionStatus) }
            )
        }

        @Test
        fun `should create mastersthesis from valid input`() {
            val bibtex = BibtexTestData.testInputs["valid mastersthesis"]!!
            val study = sut.convert(bibtex)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                study,
                source = mutableSetOf("Compendex")
            )
            assertAll(
                "bibtex",
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.MASTERSTHESIS, studyReview.studyType) },
                { assertEquals("Jian Tang", studyReview.authors) },
                { assertEquals("Spin structure of the nucleon in the asymptotic limit", studyReview.title) },
                { assertEquals("Massachusetts Institute of Technology", studyReview.venue) },
                { assertEquals(1996, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
                { assertEquals(1, studyReview.searchSources.size) },
                { assertTrue(studyReview.selectionCriteria.isEmpty()) },
                { assertTrue(studyReview.extractionCriteria.isEmpty()) },
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
            val bibtex = BibtexTestData.testInputs["valid inbook"]!!
            val study = sut.convert(bibtex)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                study,
                source = mutableSetOf("Compendex")
            )
            val expected = "Lisa A. Urry and Michael L. Cain and Steven A. Wasserman and Peter V. Minorsky and Jane B. Reece"

            assertAll(
                "bibtex",
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.INBOOK, studyReview.studyType) },
                { assertEquals(expected, studyReview.authors) },
                { assertEquals("Photosynthesis", studyReview.title) },
                { assertEquals("Campbell Biology", studyReview.venue) },
                { assertEquals(2016, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
                { assertEquals(1, studyReview.searchSources.size) },
                { assertTrue(studyReview.selectionCriteria.isEmpty()) },
                { assertTrue(studyReview.extractionCriteria.isEmpty()) },
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
            val bibtex = BibtexTestData.testInputs["valid booklet"]!!
            val study = sut.convert(bibtex)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                study,
                source = mutableSetOf("Compendex")
            )
            assertAll(
                "bibtex",
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.BOOKLET, studyReview.studyType) },
                { assertEquals("Maria Swetla", studyReview.authors) },
                { assertEquals("Canoe tours in {S}weden", studyReview.title) },
                { assertEquals("Distributed at the Stockholm Tourist Office", studyReview.venue) },
                { assertEquals(2015, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
                { assertEquals(1, studyReview.searchSources.size) },
                { assertTrue(studyReview.selectionCriteria.isEmpty()) },
                { assertTrue(studyReview.extractionCriteria.isEmpty()) },
                { assertTrue(studyReview.formAnswers.isEmpty()) },
                { assertTrue(studyReview.robAnswers.isEmpty()) },
                { assertEquals("", studyReview.comments) },
                { assertEquals(ReadingPriority.LOW, studyReview.readingPriority) },
                { assertEquals(SelectionStatus.UNCLASSIFIED, studyReview.selectionStatus) },
                { assertEquals(ExtractionStatus.UNCLASSIFIED, studyReview.extractionStatus) }
            )
        }

        @Test
        fun `should create manual from valid input`() {
            val bibtex = BibtexTestData.testInputs["valid manual"]!!
            val study = sut.convert(bibtex)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                study,
                source = mutableSetOf("Compendex")
            )
            assertAll(
                "bibtex",
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.MANUAL, studyReview.studyType) },
                { assertEquals("{R Core Team}", studyReview.authors) },
                { assertEquals("{R}: A Language and Environment for Statistical Computing", studyReview.title) },
                { assertEquals("R Foundation for Statistical Computing", studyReview.venue) },
                { assertEquals(2018, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
                { assertEquals(1, studyReview.searchSources.size) },
                { assertTrue(studyReview.selectionCriteria.isEmpty()) },
                { assertTrue(studyReview.extractionCriteria.isEmpty()) },
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
            val bibtex = BibtexTestData.testInputs["valid misc"]!!
            val study = sut.convert(bibtex)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                study,
                source = mutableSetOf("Compendex")
            )
            assertAll(
                "bibtex",
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.MISC, studyReview.studyType) },
                { assertEquals("{NASA}", studyReview.authors) },
                { assertEquals("Pluto: The 'Other' Red Planet", studyReview.title) },
                { assertEquals("\\url{https://www.nasa.gov/nh/pluto-the-other-red-planet}", studyReview.venue) },
                { assertEquals(2015, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
                { assertEquals(1, studyReview.searchSources.size) },
                { assertTrue(studyReview.selectionCriteria.isEmpty()) },
                { assertTrue(studyReview.extractionCriteria.isEmpty()) },
                { assertTrue(studyReview.formAnswers.isEmpty()) },
                { assertTrue(studyReview.robAnswers.isEmpty()) },
                { assertEquals("", studyReview.comments) },
                { assertEquals(ReadingPriority.LOW, studyReview.readingPriority) },
                { assertEquals(SelectionStatus.UNCLASSIFIED, studyReview.selectionStatus) },
                { assertEquals(ExtractionStatus.UNCLASSIFIED, studyReview.extractionStatus) }
            )
        }

        @Test
        fun `should create unpublished from valid input`() {
            val bibtex = BibtexTestData.testInputs["valid unpublished"]!!
            val study = sut.convert(bibtex)
            val studyReview = sut.convertToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                study,
                source = mutableSetOf("Compendex")
            )
            assertAll(
                "bibtex",
                { assertEquals("1", studyReview.id.toString()) },
                { assertEquals(StudyType.UNPUBLISHED, studyReview.studyType) },
                { assertEquals("Mohinder Suresh", studyReview.authors) },
                { assertEquals("Evolution: a revised theory", studyReview.title) },
                { assertEquals("Lorem Ipsum", studyReview.venue) },
                { assertEquals(2006, studyReview.year) },
                { assertEquals("Lorem Ipsum", studyReview.abstract) },
                { assertEquals(1, studyReview.searchSources.size) },
                { assertTrue(studyReview.selectionCriteria.isEmpty()) },
                { assertTrue(studyReview.extractionCriteria.isEmpty()) },
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
        fun `convertManyToStudyReview should not accept a blank bibtex entry as input`() {
            assertThrows<IllegalArgumentException> {
                sut.convertManyToStudyReview(
                    SystematicStudyId(randomUUID()),
                    SearchSessionID(randomUUID()),
                    "",
                    source = mutableSetOf("Compendex")
                )
            }
        }

        @Test
        fun `convertToStudyReview should not accept a blank bibtex entry as input`() {
            assertThrows<IllegalArgumentException> {
                sut.convert("")
            }
        }

        @Test
        fun `should throw BibtexParseException for unknown type entry`() {
            val bibtex = BibtexTestData.testInputs["unknown type of bibtex"]!!
            assertThrows<BibtexParseException> {
                sut.convert(bibtex)
            }
        }

        @Test
        fun `Should create a StudyReview list from multiple bibtex entries even when there are invalid entries`() {
            val bibtex = BibtexTestData.testInputs["multiple bibtex entries with some invalid"]!!
            val studyReviewList = sut.convertManyToStudyReview(
                SystematicStudyId(randomUUID()),
                SearchSessionID(randomUUID()),
                bibtex,
                source = mutableSetOf("Compendex")
            )
            studyReviewList.second.forEach { invalidEntryName ->
                println("Invalid BibTeX entry: $invalidEntryName")
            }
            studyReviewList.first.forEach { studyReview ->
                println("Valid StudyReview: ${studyReview.title}")
            }
            assertAll(
                {assertEquals(4, studyReviewList.first.size)},
            )
        }
    }

    @Nested
    inner class RobustParsing {

        private fun convertMany(bibtex: String) = sut.convertManyToStudyReview(
            SystematicStudyId(randomUUID()),
            SearchSessionID(randomUUID()),
            bibtex,
            source = mutableSetOf("Cochrane")
        )

        private val cochraneExport = """
            Record #1 of 3
            @article{Huey25,
            author = {Huey, SL, Mehta, NH, and Mehta, S},
            title = {Precision nutrition-based interventions for the management of obesity},
            journal = {Cochrane Database of Systematic Reviews},
            number = {1},
            year = {2025},
            publisher = {John Wiley &amp; Sons, Ltd},
            ISSN = {1465-1858},
            abstract = {Abstract - Background Meta-analysis (RR 0.8, 95% CI 0.6 to 1.0; I2 = 45%, N = 33,513, 11 trials) showed benefit.},
            DOI = {10.1002/14651858.CD015877},
            keywords = {*Pediatric Obesity [diet therapy, therapy]; Child, Preschool; Humans},
            URL = {http://dx.doi.org/10.1002/14651858.CD015877}
            }


            Record #2 of 3
            @article{Alfirevic06,
            author = {Alfirevic, Z, Devane, D, and Gyte, GML},
            title = {Continuous cardiotocography (CTG) as a form of electronic fetal monitoring},
            journal = {Cochrane Database of Systematic Reviews},
            number = {3},
            year = {2006},
            publisher = {John Wiley &amp; Sons, Ltd},
            abstract = {Abstract - Background CTG records changes in the fetal heart rate.},
            DOI = {10.1002/14651858.CD006066.pub3},
            keywords = {*Labor, Obstetric; Cardiotocography [*methods]; Female},
            URL = {http://dx.doi.org/10.1002/14651858.CD006066.pub3}
            }


            Record #3 of 3
            @article{Alkhawaja15,
            author = {Alkhawaja, S, and Martin, C},
            title = {Antenatal corticosteroids for fetal lung maturation},
            journal = {Cochrane Database of Systematic Reviews},
            year = {2015},
            abstract = {Abstract - Rationale Lorem ipsum.},
            DOI = {10.1002/14651858.CD000001}
            }
        """.trimIndent()

        @Test
        fun `should ignore text between entries such as Cochrane Record headers`() {
            val (studies, invalid) = convertMany(cochraneExport)

            assertAll(
                { assertEquals(3, studies.size) },
                { assertTrue(invalid.isEmpty(), "Unexpected invalid entries: $invalid") },
                { assertEquals("Precision nutrition-based interventions for the management of obesity", studies[0].title) },
                { assertEquals(2006, studies[1].year) },
                { assertEquals("Antenatal corticosteroids for fetal lung maturation", studies[2].title) }
            )
        }

        @Test
        fun `should parse Cochrane export with Windows line endings`() {
            val (studies, invalid) = convertMany(cochraneExport.replace("\n", "\r\n"))

            assertAll(
                { assertEquals(3, studies.size) },
                { assertTrue(invalid.isEmpty(), "Unexpected invalid entries: $invalid") },
                { assertEquals("https://doi.org/10.1002/14651858.CD015877", studies[0].doi?.value) }
            )
        }

        @Test
        fun `should not split abstract that contains commas followed by equals signs`() {
            val (studies, _) = convertMany(cochraneExport)

            assertEquals(
                "Abstract - Background Meta-analysis (RR 0.8, 95% CI 0.6 to 1.0; I2 = 45%, N = 33,513, 11 trials) showed benefit.",
                studies[0].abstract
            )
        }

        @Test
        fun `should keep fields that come after an abstract containing a percent sign`() {
            val (studies, _) = convertMany(cochraneExport)

            assertAll(
                { assertEquals("https://doi.org/10.1002/14651858.CD015877", studies[0].doi?.value) },
                { assertTrue(studies[0].keywords.isNotEmpty()) }
            )
        }

        @Test
        fun `should decode html entities in field values`() {
            val bibtex = """
                @book{Real01,
                author = {Doe, John},
                title = {A real study &amp; its follow-up},
                publisher = {John Wiley &amp; Sons, Ltd},
                year = {2020},
                abstract = {Lorem ipsum.}
                }
            """.trimIndent()

            val (studies, _) = convertMany(bibtex)

            assertAll(
                { assertEquals("John Wiley & Sons, Ltd", studies[0].venue) },
                { assertEquals("A real study & its follow-up", studies[0].title) }
            )
        }

        @Test
        fun `should keep commas inside keywords when they are separated by semicolons`() {
            val (studies, _) = convertMany(cochraneExport)

            assertEquals(
                setOf("*Pediatric Obesity [diet therapy, therapy]", "Child, Preschool", "Humans"),
                studies[0].keywords
            )
        }

        @Test
        fun `should keep authors as exported by Cochrane`() {
            val (studies, _) = convertMany(cochraneExport)

            assertEquals("Huey, SL, Mehta, NH, and Mehta, S", studies[0].authors)
        }

        @Test
        fun `should ignore full line percent comments and commented out entries`() {
            val bibtex = """
                % exported by some tool
                % @article{Ghost00,
                @article{Real01,
                author = {Doe, John},
                title = {A real study},
                journal = {Journal of Tests},
                year = {2020},
                abstract = {Lorem ipsum.}
                }
                % another comment
                @book{Real02,
                author = {Roe, Jane},
                title = {Another real study},
                publisher = {Publisher},
                year = {2021},
                abstract = {Lorem ipsum.}
                }
            """.trimIndent()

            val (studies, invalid) = convertMany(bibtex)

            assertAll(
                { assertEquals(2, studies.size) },
                { assertTrue(invalid.isEmpty(), "Unexpected invalid entries: $invalid") }
            )
        }

        @Test
        fun `should ignore comment string and preamble entries`() {
            val bibtex = """
                @comment{jabref-meta: databaseType:bibtex;}
                @string{jt = {Journal of Tests}}
                @preamble{"\newcommand{\noopsort}[1]{}"}
                @article{Real01,
                author = {Doe, John},
                title = {A real study},
                journal = {Journal of Tests},
                year = {2020},
                abstract = {Lorem ipsum.}
                }
            """.trimIndent()

            val (studies, invalid) = convertMany(bibtex)

            assertAll(
                { assertEquals(1, studies.size) },
                { assertTrue(invalid.isEmpty(), "Unexpected invalid entries: $invalid") }
            )
        }

        @Test
        fun `should not treat at sign inside abstract as a new entry`() {
            val bibtex = """
                @article{Real01,
                author = {Doe, John},
                title = {A real study},
                journal = {Journal of Tests},
                year = {2020},
                abstract = {Contact john@example.com for details.
                @article{NotAnEntry, this is just text}
                End of abstract.}
                }
            """.trimIndent()

            val (studies, invalid) = convertMany(bibtex)

            assertAll(
                { assertEquals(1, studies.size) },
                { assertTrue(invalid.isEmpty(), "Unexpected invalid entries: $invalid") },
                { assertTrue(studies[0].abstract?.contains("End of abstract.") == true) }
            )
        }

        @Test
        fun `should parse values delimited by quotes and numbers without delimiters`() {
            val bibtex = """
                @article{Real01,
                author = "Doe, John",
                title = "A real study",
                journal = {Journal of Tests},
                year = 2020,
                abstract = {Lorem ipsum.}
                }
            """.trimIndent()

            val (studies, invalid) = convertMany(bibtex)

            assertAll(
                { assertEquals(1, studies.size) },
                { assertTrue(invalid.isEmpty(), "Unexpected invalid entries: $invalid") },
                { assertEquals("Doe, John", studies[0].authors) },
                { assertEquals("A real study", studies[0].title) },
                { assertEquals(2020, studies[0].year) }
            )
        }

        @Test
        fun `should preserve nested braces in values`() {
            val bibtex = """
                @article{Real01,
                author = {Doe, John},
                title = {{Using SOA} in {Critical-Embedded} Systems},
                journal = {Journal of Tests},
                year = {2020},
                abstract = {Lorem ipsum.}
                }
            """.trimIndent()

            val (studies, _) = convertMany(bibtex)

            assertEquals("{Using SOA} in {Critical-Embedded} Systems", studies[0].title)
        }

        @Test
        fun `should report an unclosed last entry as invalid and keep the valid ones`() {
            val bibtex = """
                @article{Real01,
                author = {Doe, John},
                title = {A real study},
                journal = {Journal of Tests},
                year = {2020},
                abstract = {Lorem ipsum.}
                }

                @article{Broken02,
                author = {Roe, Jane},
                title = {A broken study},
                year = {2021},
                abstract = {Never closed
            """.trimIndent()

            val (studies, invalid) = convertMany(bibtex)

            assertAll(
                { assertEquals(1, studies.size) },
                { assertEquals(1, invalid.size) },
                { assertTrue(invalid.first().contains("Broken02")) }
            )
        }

        @Test
        fun `should return no studies and no errors when the text has no bibtex entries`() {
            val (studies, invalid) = convertMany("Record #1 of 1\nsome text without entries")

            assertAll(
                { assertTrue(studies.isEmpty()) },
                { assertTrue(invalid.isEmpty()) }
            )
        }
    }
}