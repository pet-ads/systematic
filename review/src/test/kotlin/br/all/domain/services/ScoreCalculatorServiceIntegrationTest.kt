package br.all.domain.services

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.util.*
import kotlin.test.assertEquals

@Tag("IntegrationTest")
class ScoreCalculatorServiceIntegrationTest {

    private lateinit var sut: ScoreCalculatorService
    private lateinit var bibtexConverterService: BibtexConverterService
    private lateinit var idGeneratorService: IdGeneratorService
    private val systematicStudyId = SystematicStudyId(UUID.randomUUID())
    private val searchSessionId = SearchSessionID(UUID.randomUUID())
    private val source = mutableSetOf("Test")

    @BeforeEach
    fun setup() {
        val protocolKeywords = setOf("dengue", "mosquito", "países tropicais")
        sut = ScoreCalculatorService()
        idGeneratorService = FakeIdGeneratorService
        bibtexConverterService = BibtexConverterService(idGeneratorService)
    }

    private val protocolKeywords = setOf("dengue", "mosquito", "países tropicais")

    @AfterEach
    fun teardown() {
        val fake = idGeneratorService as FakeIdGeneratorService
        fake.reset()
    }

    @Test
    fun `should assign maximum score when all keywords match in all fields`() {
        val bibtex = """
            @article{base,
                title = {Crescimento da dengue em países tropicais},
                year = {2025},
                author = {Fulano de Tal},
                journal = {Universidade X},
                abstract = {O clima atual favorece a reprodução do mosquito Aedes aegypti, aumentando o crescimento da dengue em países tropicais.},
                keywords = {dengue, mosquito, países tropicais},
                doi = {10.1234/dengue.2025.001}
            }
        """
        val studyReviews = bibtexConverterService.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source)
        val result = sut.applyScoreToManyStudyReviews(studyReviews.first, protocolKeywords).first()

        assertEquals(25, result.score)
    }

    @Test
    fun `should assign zero score when none of the keywords match`() {
        val bibtex = """
            @article{nomatch,
                title = {Tecnologias de computação quântica na medicina moderna},
                year = {2025},
                author = {Fulano de Tal},
                journal = {Universidade X},
                abstract = {Estudo de algoritmos de machine learning aplicados à genômica usando computação quântica},
                keywords = {computação quântica, machine learning, genômica},
                doi = {10.1234/dengue.2025.001}
            }
        """
        val studyReviews = bibtexConverterService.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source)
        val result = sut.applyScoreToManyStudyReviews(studyReviews.first, protocolKeywords).first()
        assertEquals(0, result.score)
    }

    @Test
    fun `should assign partial score when only some keywords match`() {
        val bibtex = """
            @article{partialmatch,
                title = {Dengue em regiões urbanas},
                year = {2025},
                author = {Fulano de Tal},
                journal = {Universidade X},
                abstract = {Análise de fatores sociais e ambientais que afetam a propagação da dengue.},
                keywords = {dengue, regiões urbanas, clima},
                doi = {10.1234/dengue.2025.001}
            }
        """
        val studyReviews = bibtexConverterService.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source)
        val result = sut.applyScoreToManyStudyReviews(studyReviews.first, protocolKeywords).first()
        assertEquals(10, result.score)
    }

    @Test
    fun `should handle study review with null abstract`() {
        val bibtex = """
            @article{nullabstract,
                title = {Crescimento da dengue em países tropicais},
                year = {2025},
                author = {Fulano de Tal},
                journal = {Universidade X},
                keywords = {dengue, mosquito, países tropicais},
                doi = {10.1234/dengue.2025.001}
            }
        """
        val studyReviews = bibtexConverterService.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source)
        val result = sut.applyScoreToManyStudyReviews(studyReviews.first, protocolKeywords).first()
        assertEquals(16, result.score)
    }

    @Test
    fun `should return list with correct size and scores`() {
        val bibtex = """
            @article{base,
                title = {Crescimento da dengue em países tropicais},
                year = {2025},
                author = {Fulano de Tal},
                journal = {Universidade X},
                abstract = {O clima atual favorece a reprodução do mosquito Aedes aegypti, aumentando o crescimento da dengue em países tropicais.},
                keywords = {dengue, mosquito, países tropicais},
                doi = {10.1234/dengue.2025.001}
            }

            @article{nomatch,
                title = {Tecnologias de computação quântica na medicina moderna},
                year = {2025},
                author = {Fulano de Tal},
                journal = {Universidade X},
                abstract = {Estudo de algoritmos de machine learning aplicados à genômica usando computação quântica},
                keywords = {computação quântica, machine learning, genômica},
                doi = {10.1234/dengue.2025.001}
            }

            @article{partialmatch,
                title = {Dengue em regiões urbanas},
                year = {2025},
                author = {Fulano de Tal},
                journal = {Universidade X},
                abstract = {Análise de fatores sociais e ambientais que afetam a propagação da dengue.},
                keywords = {dengue, regiões urbanas, clima},
                doi = {10.1234/dengue.2025.001}
            }
        """
        val studyReviews = bibtexConverterService.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source)
        val results = sut.applyScoreToManyStudyReviews(studyReviews.first, protocolKeywords)
        assertAll(
            { assertEquals(3, results.size) },
            { assertEquals(25, results[0].score) },
            { assertEquals(0, results[1].score) },
            { assertEquals(10, results[2].score) }
        )
    }

    @Test
    fun `should properly calculate the score when title contains multiple occurrences of the same keyword`() {
        val bibtex = """
            @article{base,
                title = {Dengue, crescimento da dengue dengue em países com dengue tropicais dengue},
                year = {2025},
                author = {Fulano de Tal},
                journal = {Universidade X},
                abstract = {Qualquer coisa.},
                keywords = {dengue, mosquito},
                doi = {10.1234/dengue.2025.001}
            }
        """

        val studyReviews = bibtexConverterService.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source)
        val result = sut.applyScoreToManyStudyReviews(studyReviews.first, protocolKeywords).first()

        assertEquals(29, result.score)
    }

    @Test
    fun `should properly calculate the score when abstract contains multiple occurrences of the same keyword` () {
        val bibtex = """
            @article{base,
                title = {Crescimento da dengue em países tropicais},
                year = {2025},
                author = {Fulano de Tal},
                journal = {Universidade X},
                abstract = {Dengue, o clima atual favorece dengue, a reprodução do mosquito mosquito da dengue dengue Aedes aegypti, aumentando o crescimento da dengue em países tropicais dengue.},
                keywords = {dengue, mosquito},
                doi = {10.1234/dengue.2025.001}
            }
        """

        val studyReviews = bibtexConverterService.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source)
        val result = sut.applyScoreToManyStudyReviews(studyReviews.first, protocolKeywords).first()

        assertEquals(41, result.score)
    }
}
