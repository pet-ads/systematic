package br.all.domain.services

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.infrastructure.similarity.LevenshteinSimilarityCalculatorImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Tag("IntegrationTest")
class ReviewSimilarityServiceIntegrationTest {

    private lateinit var sut: ReviewSimilarityService
    private lateinit var levenshteinCalculator: LevenshteinSimilarityCalculatorImpl
    private lateinit var converter: BibtexConverterService
    private lateinit var idGeneratorService: IdGeneratorService
    private val systematicStudyId = SystematicStudyId(UUID.randomUUID())
    private val searchSessionId = SearchSessionID(UUID.randomUUID())
    private val source = mutableSetOf("Test")

    @BeforeEach
    fun setup() {
        levenshteinCalculator = LevenshteinSimilarityCalculatorImpl()
        sut = ReviewSimilarityService(levenshteinCalculator)
        idGeneratorService = FakeIdGeneratorService
        converter = BibtexConverterService(idGeneratorService)
    }

    @AfterEach
    fun teardown() {
        val fake = idGeneratorService as FakeIdGeneratorService
        fake.reset()
    }

    @Test
    fun `should detect duplicates with high similarity in title, authors and abstract`() {
        val bibtex = """
                @article{original,
                    title = {Machine Learning Applications in Healthcare},
                    year = {2023},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.001}
                }

                @article{duplicate,
                    title = {Machine Learning Applications in Healthcare Systems},
                    year = {2023},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    abstract = {This paper explores various applications of machine learning in healthcare, with emphasis on diagnosis and treatment optimization.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.001}
                }
            """
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews)

        assertEquals(1, result.size)
        assertTrue(result.containsKey(studyReviews[0]))
        assertEquals(1, result[studyReviews[0]]!!.size)
        assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1]))
    }

    @Test
    fun `should detect duplicates with identical content`() {
        val bibtex = """
                @article{original,
                    title = {Machine Learning Applications in Healthcare},
                    year = {2023},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.001}
                }

                @article{duplicate,
                    title = {Machine Learning Applications in Healthcare},
                    year = {2023},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.001}
                }
            """
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews)

        assertEquals(1, result.size)
        assertTrue(result.containsKey(studyReviews[0]))
        assertEquals(1, result[studyReviews[0]]!!.size)
        assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1]))
    }

    @Test
    fun `should not detect duplicates when similarity is below threshold`() {
        val bibtex = """
                @article{paper1,
                    title = {Machine Learning Applications in Healthcare},
                    year = {2023},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.001}
                }

                @article{paper2,
                    title = {Deep Learning for Medical Image Analysis},
                    year = {2023},
                    author = {Robert Johnson and Sarah Williams},
                    journal = {Journal of Medical Imaging},
                    abstract = {This study presents a comprehensive review of deep learning techniques applied to medical image analysis, with a focus on MRI and CT scans.},
                    keywords = {deep learning, medical imaging, MRI},
                    doi = {10.1234/jmi.2023.002}
                }
            """
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should handle study reviews with null abstracts`() {
        val bibtex = """
                @article{original,
                    title = {Machine Learning Applications in Healthcare},
                    year = {2023},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.001}
                }

                @article{duplicate,
                    title = {Machine Learning Applications in Healthcare},
                    year = {2023},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.001}
                }
            """
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews)

        assertEquals(1, result.size)
        assertTrue(result.containsKey(studyReviews[0]))
        assertEquals(1, result[studyReviews[0]]!!.size)
        assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1]))
    }

    @Test
    fun `should handle empty list of study reviews`() {
        val result = sut.findDuplicates(emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should handle list with only one study review`() {
        val bibtex = """
                @article{single,
                    title = {Machine Learning Applications in Healthcare},
                    year = {2023},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.001}
                }
            """
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should detect multiple groups of duplicates`() {
        val bibtex = """
                @article{group1_original,
                    title = {Machine Learning Applications in Healthcare},
                    year = {2023},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.001}
                }

                @article{group1_duplicate,
                    title = {Machine Learning Applications in Healthcare},
                    year = {2023},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.001}
                }

                @article{group2_original,
                    title = {Deep Learning for Medical Image Analysis},
                    year = {2023},
                    author = {Robert Johnson and Sarah Williams},
                    journal = {Journal of Medical Imaging},
                    abstract = {This study presents a comprehensive review of deep learning techniques applied to medical image analysis, with a focus on MRI and CT scans.},
                    keywords = {deep learning, medical imaging, MRI},
                    doi = {10.1234/jmi.2023.002}
                }

                @article{group2_duplicate,
                    title = {Deep Learning for Medical Image Analysis},
                    year = {2023},
                    author = {Robert Johnson and Sarah Williams},
                    journal = {Journal of Medical Imaging},
                    abstract = {This study presents a comprehensive review of deep learning techniques applied to medical image analysis, with a focus on MRI and CT scans.},
                    keywords = {deep learning, medical imaging, MRI},
                    doi = {10.1234/jmi.2023.002}
                }

                @article{unique,
                    title = {Natural Language Processing in Clinical Settings},
                    year = {2023},
                    author = {David Brown and Lisa Chen},
                    journal = {Journal of Clinical Informatics},
                    abstract = {This article discusses the implementation of natural language processing techniques in clinical settings for improving patient care.},
                    keywords = {NLP, clinical informatics, patient care},
                    doi = {10.1234/jci.2023.003}
                }
            """
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews)

        assertEquals(2, result.size)
        assertTrue(result.containsKey(studyReviews[0]))
        assertEquals(1, result[studyReviews[0]]!!.size)
        assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1]))
        assertTrue(result.containsKey(studyReviews[2]))
        assertEquals(1, result[studyReviews[2]]!!.size)
        assertTrue(result[studyReviews[2]]!!.contains(studyReviews[3]))
        assertFalse(result.containsKey(studyReviews[4]))
    }
}
