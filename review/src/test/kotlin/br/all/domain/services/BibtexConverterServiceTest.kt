package br.all.domain.services

import br.all.domain.model.review.ReviewId
import br.all.domain.model.study.*
import br.all.domain.services.BibtexTestData.testInputs
import org.junit.jupiter.api.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BibtexConverterServiceTest {

    private lateinit var sut: BibtexConverterService
    private lateinit var idGeneratorService: IdGeneratorService

    @BeforeEach fun setup() {
        val reviewId = ReviewId(UUID.randomUUID())
        idGeneratorService = FakeIdGeneratorService
        sut = BibtexConverterService(reviewId, idGeneratorService)
    }

    @AfterEach fun teardown() {
        val fake = idGeneratorService as FakeIdGeneratorService
        fake.reset()
    }

    @Nested inner class ValidClasses(){
        @Test fun `should create studyReview from valid input`() {
            val bibtex = testInputs["valid article"]!!
            val studyReview = sut.convertManyToStudyReview(ReviewId(UUID.randomUUID()), bibtex)

            assertAll("bibtex",
                { assertEquals("1", studyReview[0].studyId.toString()) },
                { assertEquals(StudyType.ARTICLE, studyReview[0].studyType) },
                { assertEquals("Non-cooperative Games", studyReview[0].title) },
                { assertEquals(1951, studyReview[0].year) },
                { assertEquals("Nash, John", studyReview[0].authors) },
                { assertEquals("Annals of Mathematics", studyReview[0].venue) },
                { assertEquals("Lorem Ipsum", studyReview[0].abstract) },
                { assertTrue("keyword1" in studyReview[0].keywords) },
                { assertTrue("keyword2" in studyReview[0].keywords) },
                { assertEquals(2, studyReview[0].references.size) },
                { assertEquals("https://doi.org/10.1234/doi", studyReview[0].doi?.value) },
                { assertEquals(1, studyReview[0].searchSources.size) },
                { assertTrue(studyReview[0].criteria.isEmpty()) },
                { assertTrue(studyReview[0].formAnswers.isEmpty()) },
                { assertTrue(studyReview[0].qualityAnswers.isEmpty()) },
                { assertEquals("", studyReview[0].comments) },
                { assertEquals(ReadingPriority.LOW, studyReview[0].readingPriority) },
                { assertEquals(SelectionStatus.UNCLASSIFIED, studyReview[0].selectionStatus) },
                { assertEquals(ExtractionStatus.UNCLASSIFIED, studyReview[0].extractionStatus) })
        }

        @Test fun `Should create a StudyReview list from multiple bibtex entries as input`() {
            val bibtex = testInputs["multiple bibtex entries"]!!
            val studyReviewList = sut.convertManyToStudyReview(ReviewId(UUID.randomUUID()), bibtex)
            assertEquals(7, studyReviewList.size)
        }

        //TODO this test can be removed, because it tests the behaviour of another class, not the sut
       /* @Test
        fn `Should create a valid sequence of studyIds from multiple bibtex entries as input`() {
            val bibtex = testInputs["multiple bibtex entries"]!!
            val studyReviewList = sut.convertManyToStudyReview(ReviewId(UUID.randomUUID()), bibtex)

            assertAll(
                "bibtex",
                { assertEquals("1", studyReviewList[0].studyId.toString()) },
                { assertEquals("2", studyReviewList[1].studyId.toString()) },
                { assertEquals("3", studyReviewList[2].studyId.toString()) },
                { assertEquals("4", studyReviewList[3].studyId.toString()) },
                { assertEquals("5", studyReviewList[4].studyId.toString()) },
                { assertEquals("6", studyReviewList[5].studyId.toString()) },
                { assertEquals("7", studyReviewList[6].studyId.toString()) },
            )
        }*/

        @Test fun `should create article from valid input`() {
            val bibtex = testInputs["valid article"]!!
            val (type, title, year, authors, venue, abstract, keywords, references, doi) = sut.convertMany(bibtex)[0]
            assertAll(
                "bibtex",
                { assertEquals(StudyType.ARTICLE, type) },
                { assertEquals("Non-cooperative Games", title) },
                { assertEquals(1951, year) },
                { assertEquals("Nash, John", authors) },
                { assertEquals("Annals of Mathematics", venue) },
                { assertEquals("Lorem Ipsum", abstract) },
                { assertTrue("keyword1" in keywords) },
                { assertTrue("keyword2" in keywords) },
                { assertEquals(2, references.size) },
                { assertEquals("https://doi.org/10.1234/doi", doi?.value) },
            )
        }

        @Test fun `should create inproceedings from valid input`() {
            val bibtex = testInputs["valid inproceedings"]!!
            val (type, title, year, authors, venue, abstract, _, references, doi) = sut.convertMany(bibtex)[0]
            assertAll(
                "bibtex",
                { assertEquals(StudyType.INPROCEEDINGS, type) },
                { assertEquals("Onofre {Trindade Júnior}", authors) },
                { assertEquals("{Using SOA in Critical-Embedded Systems}", title) },
                { assertEquals("Proceedings of the 4${'$'}^{th}${'$'}  IEEE (CPSCom'11)", venue) },
                { assertEquals(2011, year) },
                { assertEquals("Lorem ipsum", abstract) },
                { assertEquals(listOf("ref3", "ref4"), references) },
                { assertEquals(Doi("https://doi.org/10.1021/ci025584y"), doi) },
            )
        }

        @Test fun `should create techreport from valid input`() {
            val bibtex = testInputs["valid techreport"]!!
            val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]
            val expected = "Uma abordagem apoiada por linguagens específicas de domínio " +
            "para a criação de linhas de produto de software embarcado"

            assertAll(
                "bibtex",
                { assertEquals(StudyType.TECHREPORT, type) },
                { assertEquals("Rafael Serapilha Durelli", authors) },
                { assertEquals(expected, title) },
                { assertEquals("Universidade Federal de São Carlos (UFSCar)", venue) },
                { assertEquals(2011, year) },
                { assertEquals("Lorem Ipsum", abstract) },
            )
        }

        @Test fun `should create book from valid input`() {
            val bibtex = testInputs["valid book"]!!
            val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]

            assertAll(
                "bibtex",
                { assertEquals(StudyType.BOOK, type) },
                { assertEquals("Len Bass and Paul Clements and Rick Kazman", authors) },
                { assertEquals("Software Architecture in Practice", title) },
                { assertEquals("Addison-Wesley", venue) },
                { assertEquals(2012, year) },
                { assertEquals("Lorem Ipsum", abstract) },
            )
        }

        @Test fun `should create proceedings from valid input`() {
            val bibtex = testInputs["valid proceedings"]!!
            val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]

            val expected = "Proceedings of the 17th International Conference on Computation " +
                    "and Natural Computation, Fontainebleau, France"

            assertAll(
                "bibtex",
                { assertEquals(StudyType.PROCEEDINGS, type) },
                { assertEquals("Susan Stepney and Sergey Verlan", authors) },
                { assertEquals(expected, title) },
                { assertEquals("Springer", venue) },
                { assertEquals(2018, year) },
                { assertEquals("Lorem Ipsum", abstract) },
            )
        }

        @Test fun `should create phdthesis from valid input`() {
            val bibtex = testInputs["valid phdthesis"]!!
            val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]

            assertAll(
                "bibtex",
                { assertEquals(StudyType.PHDTHESIS, type) },
                { assertEquals("Rempel, Robert Charles", authors) },
                { assertEquals("Relaxation Effects for Coupled Nuclear Spins", title) },
                { assertEquals("Stanford University", venue) },
                { assertEquals(1956, year) },
                { assertEquals("Lorem Ipsum", abstract) },
            )
        }

        @Test fun `should create mastersthesis from valid input`() {
            val bibtex = testInputs["valid mastersthesis"]!!
            val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]

            assertAll(
                "bibtex",
                { assertEquals(StudyType.MASTERSTHESIS, type) },
                { assertEquals("Jian Tang", authors) },
                { assertEquals("Spin structure of the nucleon in the asymptotic limit", title) },
                { assertEquals("Massachusetts Institute of Technology", venue) },
                { assertEquals(1996, year) },
                { assertEquals("Lorem Ipsum", abstract) },
            )
        }

        @Test fun `should create inbook from valid input`() {
            val bibtex = testInputs["valid inbook"]!!
            val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]

            val expected = "Lisa A. Urry and Michael L. Cain and Steven A. Wasserman " +
                "and Peter V. Minorsky and Jane B. Reece"

            assertAll(
                "bibtex",
                { assertEquals(StudyType.INBOOK, type) },
                { assertEquals(expected, authors ) },
                { assertEquals("Photosynthesis", title) },
                { assertEquals("Campbell Biology", venue) },
                { assertEquals(2016, year) },
                { assertEquals("Lorem Ipsum", abstract) },
            )
        }

        @Test fun `should create booklet from valid input`() {
            val bibtex = testInputs["valid booklet"]!!
            val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]

            assertAll(
                "bibtex",
                { assertEquals(StudyType.BOOKLET, type) },
                { assertEquals("Maria Swetla", authors) },
                { assertEquals("Canoe tours in {S}weden", title) },
                { assertEquals("Distributed at the Stockholm Tourist Office", venue) },
                { assertEquals(2015, year) },
                { assertEquals("Lorem Ipsum", abstract) },
            )
        }

        @Test fun `should create manual from valid input`() {
            val bibtex = testInputs["valid manual"]!!
            val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]

            assertAll(
                "bibtex",
                { assertEquals(StudyType.MANUAL, type) },
                { assertEquals("{R Core Team}", authors) },
                { assertEquals("{R}: A Language and Environment for Statistical Computing", title) },
                { assertEquals("R Foundation for Statistical Computing", venue) },
                { assertEquals(2018, year) },
                { assertEquals("Lorem Ipsum", abstract) },
            )
        }

        @Test fun `should create misc from valid input`() {
            val bibtex = testInputs["valid misc"]!!
            val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]

            assertAll(
                "bibtex",
                { assertEquals(StudyType.MISC, type) },
                { assertEquals("{NASA}", authors) },
                { assertEquals("Pluto: The 'Other' Red Planet", title) },
                { assertEquals("\\url{https://www.nasa.gov/nh/pluto-the-other-red-planet}", venue) },
                { assertEquals(2015, year) },
                { assertEquals("Lorem Ipsum", abstract) },
            )
        }

        @Test fun `should create unpublished from valid input`() {
            val bibtex = testInputs["valid unpublished"].toString()
            val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]

            assertAll(
                "bibtex",
                { assertEquals(StudyType.UNPUBLISHED, type) },
                { assertEquals("Mohinder Suresh", authors) },
                { assertEquals("Evolution: a revised theory", title) },
                { assertEquals("Lorem Ipsum", venue) },
                { assertEquals(2006, year) },
                { assertEquals("Lorem Ipsum", abstract) },
            )
        }

        @Test fun `should create a list from multiple different valid bibtex entries as input`() {
            val bibtex = testInputs["multiple bibtex entries"]!!
            val studyList = sut.convertMany(bibtex)
            assertEquals(7, studyList.size)
        }
    }

    @Nested inner class InvalidClasses() {
        @Test fun `convertManyToStudyReview should not accept a blank bibtex entry as input`() {
            assertThrows<IllegalArgumentException> {
                sut.convertManyToStudyReview(ReviewId(UUID.randomUUID()), "")
            }
        }

        @Test fun `should throw IllegalArgumentException for empty input`() {
            assertThrows<IllegalArgumentException> { sut.convertMany("") }
        }

        @Test fun `should throw IllegalArgumentException for unknown type entry`() {
            val bibtex = testInputs["unknown type of bibtex"]!!
            assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
        }

        @Test fun `should throw IllegalArgumentException for invalid title entry`() {
            val bibtex = testInputs["invalid title"].toString()
            assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
        }

        @Test fun `should throw IllegalArgumentException for invalid author entry`() {
            val bibtex = testInputs["invalid authors"]!!
            assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
        }

        @Test fun `should throw IllegalArgumentException for invalid year entry`() {
            val bibtex = testInputs["invalid year"]!!
            assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
        }

        @Test fun `should throw IllegalArgumentException for invalid venue entry`() {
            val bibtex = testInputs["invalid venue"]!!
            assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
        }

        @Test fun `should throw IllegalArgumentException for invalid abstract entry`() {
            val bibtex = testInputs["invalid abstract"]!!
            assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
        }

        @Test fun `should throw IllegalArgumentException for invalid doi`() {
            val bibtex = testInputs["invalid doi"]!!
            assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
        }

    }


    //TODO i did not get it. You are testing default values but checking the non-default
    /*
    @Test fun `should generate default values for optional parameter`() {
         val bibtex = testInputs["article missing optional fields"]!!
         val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]
         assertAll(
             "bibtex",
             { assertEquals(StudyType.ARTICLE, type) },
             { assertEquals("The independence of the continuum hypothesis", title) },
             { assertEquals(1963, year) },
             { assertEquals("P. J. Cohen", authors) },
             { assertEquals("Proceedings of the National Academy of Sciences", venue) },
             { assertEquals("Lorem Ipsum", abstract) },
         )
     }*/
}
