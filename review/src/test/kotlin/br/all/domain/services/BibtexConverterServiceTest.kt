package br.all.domain.services

import br.all.domain.model.review.ReviewId
import br.all.domain.model.study.*
import br.all.infrastructure.shared.SequenceGeneratorService
import br.all.infrastructure.study.StudyReviewIdGeneratorService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BibtexConverterServiceTest {

    private lateinit var sut: BibtexConverterService
    private lateinit var idGeneratorService: IdGeneratorService

    @BeforeEach
    fun setup() {
        val reviewId = ReviewId(UUID.randomUUID())
        idGeneratorService = mock(IdGeneratorService::class.java)
        sut = BibtexConverterService(reviewId, idGeneratorService)
        `when`(idGeneratorService.next())
            .thenReturn(1L)
            .thenReturn(2L)
            .thenReturn(3L)
            .thenReturn(4L)
            .thenReturn(5L)
            .thenReturn(6L)
            .thenReturn(7L)
    }

    @Test
    fun `convertManyToStudyReview should not accept a blank bibtex entry as input`() {

        assertThrows<IllegalArgumentException> {
            sut.convertManyToStudyReview(ReviewId(UUID.randomUUID()),"")
        }
    }

    @Test
    fun `should create studyReview from valid input`() {
        val bibtex = testInputs["valid article"].toString()

        val studyReview = sut.convertManyToStudyReview(ReviewId(UUID.randomUUID()), bibtex)

        assertAll(
            "bibtex",
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
            { assertEquals(ExtractionStatus.UNCLASSIFIED, studyReview[0].extractionStatus) }
        )
    }

    @Test
    fun `Should create a StudyReview list from multiple bibtex entries as input`() {
        val bibtex = testInputs["multiple bibtex entries"].toString()

        val studyReviewList = sut.convertManyToStudyReview(ReviewId(UUID.randomUUID()), bibtex)

        assertEquals(7, studyReviewList.size)
    }

    @Test
    fun `Should create a valid sequence of studyIds from multiple bibtex entries as input`() {
        val bibtex = testInputs["multiple bibtex entries"].toString()

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
    }

    @Test
    fun `should not accept a blank entry, resulting in a IllegalArgumentException`() {

        assertThrows<IllegalArgumentException> { sut.convertMany("") }
    }

    @Test
    fun `should not accept a unknown type entry, resulting in a IllegalArgumentException`() {

        val bibtex = testInputs["unknown type of bibtex"].toString()

        assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
    }

    @Test
    fun `should not accept invalid title entry, resulting in a IllegalArgumentException`() {

        val bibtex = testInputs["invalid title"].toString()

        assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
    }

    @Test
    fun `should not accept a invalid author entry, resulting in a IllegalArgumentException`() {

        val bibtex = testInputs["invalid authors"].toString()

        assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
    }

    @Test
    fun `should not accept a invalid year entry, resulting in a IllegalArgumentException`() {

        val bibtex = testInputs["invalid year"].toString()

        assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
    }

    @Test
    fun `should not accept a invalid venue entry, resulting in a IllegalArgumentException`() {

        val bibtex = testInputs["invalid venue"].toString()

        assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
    }

    @Test
    fun `sshould not accept a invalid abstract entry, resulting in a IllegalArgumentException`() {

        val bibtex = testInputs["invalid abstract"].toString()

        assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
    }

    @Test
    fun `should not accept a invalid doi entry, resulting in a IllegalArgumentException`() {

        val bibtex = testInputs["invalid doi"].toString()

        assertThrows<IllegalArgumentException> { sut.convertMany(bibtex) }
    }

    @Test
    fun `should create study from missing optional fields input`() {

        val bibtex = testInputs["article missing optional fields"].toString()

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
    }

    @Test
    fun `should create article from valid input`() {

        val bibtex = testInputs["valid article"].toString()

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

    @Test
    fun `should create inproceedings from valid input`() {

        val bibtex = testInputs["valid inproceedings"].toString()

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

    @Test
    fun `should create techreport from valid input`() {

        val bibtex = testInputs["valid techreport"].toString()

        val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]

        assertAll(
            "bibtex",
            { assertEquals(StudyType.TECHREPORT, type) },
            { assertEquals("Rafael Serapilha Durelli", authors) },
            {
                assertEquals(
                    "Uma abordagem apoiada por linguagens específicas de domínio " +
                            "para a criação de linhas de produto de software embarcado", title
                )
            },
            { assertEquals("Universidade Federal de São Carlos (UFSCar)", venue) },
            { assertEquals(2011, year) },
            { assertEquals("Lorem Ipsum", abstract) },
        )
    }

    @Test
    fun `should create book from valid input`() {

        val bibtex = testInputs["valid book"].toString()

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

    @Test
    fun `should create proceedings from valid input`() {

        val bibtex = testInputs["valid proceedings"].toString()

        val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]

        assertAll(
            "bibtex",
            { assertEquals(StudyType.PROCEEDINGS, type) },
            { assertEquals("Susan Stepney and Sergey Verlan", authors) },
            {
                assertEquals(
                    "Proceedings of the 17th International Conference on Computation " +
                            "and Natural Computation, Fontainebleau, France", title
                )
            },
            { assertEquals("Springer", venue) },
            { assertEquals(2018, year) },
            { assertEquals("Lorem Ipsum", abstract) },
        )
    }

    @Test
    fun `should create phdthesis from valid input`() {

        val bibtex = testInputs["valid phdthesis"].toString()

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

    @Test
    fun `should create mastersthesis from valid input`() {

        val bibtex = testInputs["valid mastersthesis"].toString()

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

    @Test
    fun `should create inbook from valid input`() {

        val bibtex = testInputs["valid inbook"].toString()

        val (type, title, year, authors, venue, abstract) = sut.convertMany(bibtex)[0]

        assertAll(
            "bibtex",
            { assertEquals(StudyType.INBOOK, type) },
            {
                assertEquals(
                    "Lisa A. Urry and Michael L. Cain and Steven A. Wasserman " +
                            "and Peter V. Minorsky and Jane B. Reece", authors
                )
            },
            { assertEquals("Photosynthesis", title) },
            { assertEquals("Campbell Biology", venue) },
            { assertEquals(2016, year) },
            { assertEquals("Lorem Ipsum", abstract) },
        )
    }

    @Test
    fun `should create booklet from valid input`() {

        val bibtex = testInputs["valid booklet"].toString()

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

    @Test
    fun `should create manual from valid input`() {

        val bibtex = testInputs["valid manual"].toString()

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

    @Test
    fun `should create misc from valid input`() {

        val bibtex = testInputs["valid misc"].toString()

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

    @Test
    fun `should create unpublished from valid input`() {

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

    @Test
    fun `should create a list from multiple different valid bibtex entries as input`() {

        val bibtex = testInputs["multiple bibtex entries"].toString()

        val studyList = sut.convertMany(bibtex)

        assertEquals(7, studyList.size)

    }

    private val testInputs = mapOf(
        "unknown type of bibtex" to """
            @{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                author = {Nash, John},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "invalid title" to """
            @article{nash51,
                year = {1951},
                author = {Nash, John},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "invalid year" to """
            @article{nash51,
                title = {Non-cooperative Games},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "invalid abstract" to """
            @article{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                journal = {Annals of Mathematics},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "invalid venue" to """
            @article{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "invalid doi" to """
            @article{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
            }
        """,

        "invalid authors" to """
            @article{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "article missing optional fields" to """
            @article{CitekeyArticle,
                title = {The independence of the continuum hypothesis},
                year = {1963},
                author = {P. J. Cohen},
                journal = {Proceedings of the National Academy of Sciences},
                abstract = {Lorem Ipsum},
            }
        """,

        "valid article" to """
            @article{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                author = {Nash, John},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "valid inproceedings" to """
            @INPROCEEDINGS{Rodrigues11MOSA,
              author    = {Onofre {Trindade Júnior}},
              title     = {{Using SOA in Critical-Embedded Systems}},
              booktitle = {Proceedings of the 4${'$'}^{th}${'$'}  IEEE (CPSCom'11)},
              year      = {2011},
              pages     = {733-738},
              address   = {Dalian, China},
              abstract  = {Lorem ipsum},
              references = {ref3, ref4},
              doi        = {10.1021/ci025584y}
            }
        """,

        "valid techreport" to """
            @Techreport{Durelli2011LPSRM,
                author    = {Rafael Serapilha Durelli},
                title     = {Uma abordagem apoiada por linguagens específicas de domínio para a criação de linhas de produto de software embarcado},
                institution  = {Universidade Federal de São Carlos (UFSCar)},
                year      = {2011},
                type      = {Dissertação de Mestrado},
                address   = {São Carlos, SP},
                abstract  = {Lorem Ipsum}
            }
        """,

        "valid book" to """
            @BOOK{Bass03SAPRr,
              title      = {Software Architecture in Practice},
              publisher  = {Addison-Wesley},
              year       = {2012},
              series     = {SEI Series in Software Engineering},
              edition    = {3},
              author     = {Len Bass and Paul Clements and Rick Kazman},
              abstract    = {Lorem Ipsum}
            }
        """,

        "valid proceedings" to """
            @proceedings{CitekeyProceedings,
              editor    = {Susan Stepney and Sergey Verlan},
              title     = {Proceedings of the 17th International Conference on Computation and Natural Computation, Fontainebleau, France},
              series    = {Lecture Notes in Computer Science},
              volume    = {10867},
              publisher = {Springer},
              address   = {Cham, Switzerland},
              year      = {2018},
              abstract  = {Lorem Ipsum}
            }
        """,

        "valid phdthesis" to """
            @phdthesis{CitekeyPhdthesis,
              author  = {Rempel, Robert Charles},
              title   = {Relaxation Effects for Coupled Nuclear Spins},
              school  = {Stanford University},
              address = {Stanford, CA},
              year    = {1956},
              month   = {jun}
              abstract = {Lorem Ipsum}
            }
        """,

        "valid mastersthesis" to """
            @mastersthesis{CitekeyMastersthesis,
              author  = {Jian Tang},
              title   = {Spin structure of the nucleon in the asymptotic limit},
              school  = {Massachusetts Institute of Technology},
              year    = {1996},
              address = {Cambridge, MA},
              month   = {sep}
              abstract = {Lorem Ipsum}
            }
        """,

        "valid inbook" to """
            @inbook{CitekeyInbook,
              author    = {Lisa A. Urry and Michael L. Cain and Steven A. Wasserman and Peter V. Minorsky and Jane B. Reece},
              title     = {Photosynthesis},
              booktitle = {Campbell Biology},
              year      = {2016},
              publisher = {Pearson},
              address   = {New York, NY},
              pages     = {187--221}
              abstract  = {Lorem Ipsum}
            }
        """,

        "valid booklet" to """
            @booklet{CitekeyBooklet,
              title        = {Canoe tours in {S}weden},
              author       = {Maria Swetla}, 
              howpublished = {Distributed at the Stockholm Tourist Office},
              month        = {jul},
              year         = {2015},
              abstract     = {Lorem Ipsum}
            }
        """,

        "valid manual" to """
            @manual{CitekeyManual,
              title        = {{R}: A Language and Environment for Statistical Computing},
              author       = {{R Core Team}},
              organization = {R Foundation for Statistical Computing},
              address      = {Vienna, Austria},
              year         = {2018},
              abstract     = {Lorem Ipsum}
            }
        """,

        "valid misc" to """
            @misc{CitekeyMisc,
              title        = {Pluto: The 'Other' Red Planet},
              author       = {{NASA}},
              howpublished = {\url{https://www.nasa.gov/nh/pluto-the-other-red-planet}},
              year         = {2015},
              note         = {Accessed: 2018-12-06}
              abstract     = {Lorem Ipsum}
            }
        """,

        "valid unpublished" to """
            @unpublished{CitekeyUnpublished,
              author = {Mohinder Suresh},
              title  = {Evolution: a revised theory},
              year   = {2006},
              journal = {Lorem Ipsum},
              abstract = {Lorem Ipsum}
            }
        """,

        "multiple bibtex entries" to """
            @article{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                author = {Nash, John},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
            
            @INPROCEEDINGS{Rodrigues11MOSA,
              author    = {Onofre {Trindade Júnior}},
              title     = {{Using SOA in Critical-Embedded Systems}},
              booktitle = {Proceedings of the 4${'$'}^{th}${'$'}  IEEE (CPSCom'11)},
              year      = {2011},
              pages     = {733-738},
              address   = {Dalian, China},
              abstract  = {Lorem ipsum},
              references = {ref3, ref4},
              doi        = {10.1021/ci025584y}
            }
            
            @Techreport{Durelli2011LPSRM,
                author    = {Rafael Serapilha Durelli},
                title     = {Uma abordagem apoiada por linguagens específicas de domínio para a criação de linhas de produto de software embarcado},
                institution  = {Universidade Federal de São Carlos (UFSCar)},
                year      = {2011},
                type      = {Dissertação de Mestrado},
                address   = {São Carlos, SP},
                abstract  = {Lorem Ipsum}
            }
            
            @BOOK{Bass03SAPRr,
              title      = {Software Architecture in Practice},
              publisher  = {Addison-Wesley},
              year       = {2012},
              series     = {SEI Series in Software Engineering},
              edition    = {3},
              author     = {Len Bass and Paul Clements and Rick Kazman},
              abstract    = {Lorem Ipsum}
            }
            
            @proceedings{CitekeyProceedings,
              editor    = {Susan Stepney and Sergey Verlan},
              title     = {Proceedings of the 17th International Conference on Computation and Natural Computation, Fontainebleau, France},
              series    = {Lecture Notes in Computer Science},
              volume    = {10867},
              publisher = {Springer},
              address   = {Cham, Switzerland},
              year      = {2018},
              abstract  = {Lorem Ipsum}
            }
            
            @phdthesis{CitekeyPhdthesis,
              author  = {Rempel, Robert Charles},
              title   = {Relaxation Effects for Coupled Nuclear Spins},
              school  = {Stanford University},
              address = {Stanford, CA},
              year    = {1956},
              month   = {jun}
              abstract = {Lorem Ipsum}
            }
            
            @mastersthesis{CitekeyMastersthesis,
              author  = {Jian Tang},
              title   = {Spin structure of the nucleon in the asymptotic limit},
              school  = {Massachusetts Institute of Technology},
              year    = {1996},
              address = {Cambridge, MA},
              month   = {sep}
              abstract = {Lorem Ipsum}
            }
        """
    )
}
