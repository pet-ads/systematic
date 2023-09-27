import br.all.domain.model.study.Doi
import br.all.domain.services.BibtexConverterService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BibtexConverterServiceTest {

    private lateinit var sut : BibtexConverterService

    @BeforeEach
    fun setup() {
        sut = BibtexConverterService()
    }

    //TODO Fix the code to make all tests pass. Add other tests to cope with the new study types, missing and optional fields
    @Test
    fun testConvertBibtexToStudyList() {
        //TODO create supporting methods to avoid clutter or spreading repeated testing input over multiple tests. Maybe create a map of test entries and use it
        val bibtex = """
            @article{Smith2021,
                title = {A New Study},
                year = {2021},
                author = {John Smith},
                journal = {Journal of Studies},
                abstract = {This is the abstract},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
            @article{Doe2022,
                title = {Another Study},
                year = {2022},
                author = {Jane Doe},
                journal = {Another Journal},
                abstract = {Another abstract},
                keywords = {keyword3, keyword4},
                references = {ref3, ref4},
                doi = {10.5678/doi}
            }
        """.trimIndent()

        val converter = BibtexConverterService() // TODO use @BeforeEach to instantiate all common text fixture
        val studies = sut.convertMany(bibtex)

        assertEquals(2, studies.size)

        val study1 = studies[0]
        assertEquals("A New Study", study1.title)
        assertEquals(2021, study1.year)
        assertEquals("John Smith", study1.authors)
        assertEquals("Journal of Studies", study1.venue)
        assertEquals("This is the abstract", study1.abstract)
        assertTrue("keyword1" in study1.keywords)
        assertTrue("keyword2" in study1.keywords)
        assertEquals(2, study1.references.size)
        assertEquals("10.1234/doi", study1.doi?.value)

        val study2 = studies[1]
        //TODO use assertAll instead of independent hard assertions (if the first fails, the following will not be tested)
        assertEquals("Another Study", study2.title)
        assertEquals(2022, study2.year)
        assertEquals("Jane Doe", study2.authors)
        assertEquals("Another Journal", study2.venue)
        assertEquals("Another abstract", study2.abstract)
        assertTrue("keyword3" in study2.keywords)
        assertTrue("keyword4" in study2.keywords)
        assertEquals(2, study2.references.size)
        assertEquals("10.5678/doi", study2.doi?.value)
    }

    //TODO notice the next test method name using ` ` to improve legibility
    @Test
    fun testConvertBibtexToStudyListWithEmptyInput() {
        val bibtex = "" // TODO keep it simple
        val converter = BibtexConverterService()
        assertThrows<IllegalArgumentException> {converter.convertMany("")}
    }

    @Test
    fun `should create improceedings from valid input`(){
        val bibtex = """
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
            """.trimIndent()

        val (title, year, authors, venue, abstract, keywords, references, doi) = sut.convertMany(bibtex)[0]

        assertAll("bibtex",
            { assertEquals("Onofre {Trindade Júnior}", authors) },
            { assertEquals("Using SOA in Critical-Embedded Systems", title) },
            { assertEquals("Proceedings of the 4${'$'}^{th}${'$'}  IEEE (CPSCom'11)", venue) },
            { assertEquals(2011, year) },
            { assertEquals("Lorem ipsum", abstract) },
            { assertEquals(setOf("keyword3, keyword4"), keywords) },
            { assertEquals(listOf("ref3", "ref4"), references) },
            { assertEquals(Doi("https://doi.org/10.1021/ci025584y"), doi) },
        )
    }

    //TODO real test enties
    /*
@Techreport{Durelli2011LPSRM,
  author    = {Rafael Serapilha Durelli},
  title     = {Uma abordagem apoiada por linguagens específicas de domínio para a criação de linhas de produto de software embarcado},
  institution  = {Universidade Federal de São Carlos (UFSCar)},
  year      = {2011},
  type      = {Dissertação de Mestrado},
  address   = {São Carlos, SP},
}

@ARTICLE{Arsanjani07S3,
  author     = {Arsanjani, A. and Zhang, L.-J. and Ellis, M. and Allam, A. and Channabasavaiah, K.},
  title      = {{S3}: {A} service-oriented reference architecture},
  journal    = {{IT} Professional},
  year       = {2007},
  volume     = {9},
  pages      = {10-17},
  number     = {3},
  issn       = {15209202},
}

@MISC{AGX12Tiriba,
  author    = {{AGX}},
  title     = {{VANT Tiriba}},
  howpublished = {Online},
  year      = {2012},
  note      = {\url{http://www.agx.com.br/} (Acessado em 15/08/2012)},
}

@INPROCEEDINGS{Azaiez04GAMA,
  author    = {S. Azaiez and  F. Oquendo},
  title     = {{GAMA}: Towards Architecture-centric Software Engineering of Mobile Agent Systems},
  booktitle = {Proceedings of the 3$^{rd}$ International Workshop on Software Engineering for Large-Scale Multi-Agent Systems (SELMAS'04) at the 26$^{th}$ IEEE/ACM International Conference on Software Engineering (ICSE'04)},
  pages     = {56-65},
  year      = {2004},
  address   = {Edinburgh, UK},
  month     = {May.}
}

@BOOK{Bass03SAPRr,
  title      = {Software Architecture in Practice},
  publisher  = {Addison-Wesley},
  year       = {2012},
  series     = {SEI Series in Software Engineering},
  edition    = {3},
  author     = {Len Bass and Paul Clements and Rick Kazman},
}
 */
}
