import br.all.domain.services.BibtexToStudyReviewListConverterService
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BibtexToStudyReviewListConverterServiceTest {

    @Test
    fun testConvertBibtexToStudyList() {
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

        val converter = BibtexToStudyReviewListConverterService()
        val studies = converter.convertBibtexToStudyList(bibtex)

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

    @Test
    fun testConvertBibtexToStudyListWithEmptyInput() {
        val bibtex = ""
        val converter = BibtexToStudyReviewListConverterService()
        val studies = converter.convertBibtexToStudyList(bibtex)
        assertEquals(0, studies.size)
    }
}
