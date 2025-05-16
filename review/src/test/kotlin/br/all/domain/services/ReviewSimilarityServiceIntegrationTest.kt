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
    /*
        These tests were all made
        with the all the thresholds set to 0.8,
        keep in mind that if you change the threshold
        theses tests might break
     */

    private lateinit var sut: ReviewSimilarityService
    private lateinit var levenshteinCalculator: LevenshteinSimilarityCalculatorImpl
    private lateinit var converter: BibtexConverterService
    private lateinit var idGeneratorService: IdGeneratorService
    private val systematicStudyId = SystematicStudyId(UUID.randomUUID())
    private val searchSessionId = SearchSessionID(UUID.randomUUID())
    private val source = mutableSetOf("Test")
    private val titleThreshold = 0.8
    private val abstractThreshold = 0.8
    private val authorsThreshold = 0.8

    @BeforeEach
    fun setup() {
        levenshteinCalculator = LevenshteinSimilarityCalculatorImpl()
        sut = ReviewSimilarityService(levenshteinCalculator, titleThreshold, abstractThreshold, authorsThreshold)
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
        val result = sut.findDuplicates(studyReviews, emptyList())

        assertEquals(1, result.size)
        assertTrue(result.containsKey(studyReviews[0]))
        assertEquals(1, result[studyReviews[0]]!!.size)
        assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1]))

        assertEquals(br.all.domain.model.study.SelectionStatus.DUPLICATED, studyReviews[1].selectionStatus)
        assertEquals(br.all.domain.model.study.ExtractionStatus.DUPLICATED, studyReviews[1].extractionStatus)

        assertTrue(studyReviews[0].searchSources.contains("Test"))
        assertEquals(1, studyReviews[0].searchSources.size)
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
        val result = sut.findDuplicates(studyReviews, emptyList())

        assertEquals(1, result.size)
        assertTrue(result.containsKey(studyReviews[0]))
        assertEquals(1, result[studyReviews[0]]!!.size)
        assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1]))

        assertEquals(br.all.domain.model.study.SelectionStatus.DUPLICATED, studyReviews[1].selectionStatus)
        assertEquals(br.all.domain.model.study.ExtractionStatus.DUPLICATED, studyReviews[1].extractionStatus)

        assertTrue(studyReviews[0].searchSources.contains("Test"))
        assertEquals(1, studyReviews[0].searchSources.size)
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
        val result = sut.findDuplicates(studyReviews, emptyList())

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
        val result = sut.findDuplicates(studyReviews, emptyList())

        assertEquals(1, result.size)
        assertTrue(result.containsKey(studyReviews[0]))
        assertEquals(1, result[studyReviews[0]]!!.size)
        assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1]))

        assertEquals(br.all.domain.model.study.SelectionStatus.DUPLICATED, studyReviews[1].selectionStatus)
        assertEquals(br.all.domain.model.study.ExtractionStatus.DUPLICATED, studyReviews[1].extractionStatus)

        assertTrue(studyReviews[0].searchSources.contains("Test"))
        assertEquals(1, studyReviews[0].searchSources.size)
    }

    @Test
    fun `should handle empty list of study reviews`() {
        val result = sut.findDuplicates(emptyList(), emptyList())

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
        val result = sut.findDuplicates(studyReviews, emptyList())

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
        val result = sut.findDuplicates(studyReviews, emptyList())

        assertEquals(2, result.size)
        assertTrue(result.containsKey(studyReviews[0]))
        assertEquals(1, result[studyReviews[0]]!!.size)
        assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1]))
        assertTrue(result.containsKey(studyReviews[2]))
        assertEquals(1, result[studyReviews[2]]!!.size)
        assertTrue(result[studyReviews[2]]!!.contains(studyReviews[3]))
        assertFalse(result.containsKey(studyReviews[4]))

        assertEquals(br.all.domain.model.study.SelectionStatus.DUPLICATED, studyReviews[1].selectionStatus)
        assertEquals(br.all.domain.model.study.ExtractionStatus.DUPLICATED, studyReviews[1].extractionStatus)
        assertEquals(br.all.domain.model.study.SelectionStatus.DUPLICATED, studyReviews[3].selectionStatus)
        assertEquals(br.all.domain.model.study.ExtractionStatus.DUPLICATED, studyReviews[3].extractionStatus)

        assertTrue(studyReviews[0].searchSources.contains("Test"))
        assertEquals(1, studyReviews[0].searchSources.size)
        assertTrue(studyReviews[2].searchSources.contains("Test"))
        assertEquals(1, studyReviews[2].searchSources.size)
    }

    @Test
    fun `should skip levenshtein calculation when years are not the same`() {
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
                    title = {Extension study of: Machine Learning Applications in Healthcare Systems},
                    year = {2024},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    abstract = {This paper is an updated version of an old study that explores various applications of machine learning in healthcare, with emphasis on diagnosis and treatment optimization.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.001}
                }
            """

        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews,emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun `testing a lot of combination of possibilities at the same time`() {
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

                @article{group1_similar_title,
                    title = {Machine Learning Applications in Healthcare Systems},
                    year = {2023},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.002}
                }

                @article{group1_similar_abstract,
                    title = {Machine Learning Applications in Healthcare},
                    year = {2023},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    abstract = {This paper explores various applications of ML in healthcare, with focus on diagnosis and treatment optimization approaches.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.003}
                }

                @article{group1_different_year,
                    title = {Machine Learning Applications in Healthcare},
                    year = {2022},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2022.001}
                }

                @article{group1_different_authors,
                    title = {Machine Learning Applications in Healthcare},
                    year = {2023},
                    author = {Robert Johnson and Sarah Williams},
                    journal = {Journal of Medical Informatics},
                    abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.004}
                }

                @article{group1_different_abstract,
                    title = {Machine Learning Applications in Healthcare},
                    year = {2023},
                    author = {John Smith and Maria Garcia},
                    journal = {Journal of Medical Informatics},
                    abstract = {This study presents a completely different approach to healthcare analytics using statistical methods and traditional algorithms.},
                    keywords = {machine learning, healthcare, diagnosis},
                    doi = {10.1234/jmi.2023.005}
                }

                @article{group2_original,
                    title = {Deep Learning for Medical Image Analysis},
                    year = {2023},
                    author = {Robert Johnson and Sarah Williams},
                    journal = {Journal of Medical Imaging},
                    abstract = {This study presents a comprehensive review of deep learning techniques applied to medical image analysis, with a focus on MRI and CT scans.},
                    keywords = {deep learning, medical imaging, MRI},
                    doi = {10.1234/jmi.2023.006}
                }

                @article{group2_duplicate,
                    title = {Deep Learning for Medical Image Analysis},
                    year = {2023},
                    author = {Robert Johnson and Sarah Williams},
                    journal = {Journal of Medical Imaging},
                    abstract = {This study presents a comprehensive review of deep learning techniques applied to medical image analysis, with a focus on MRI and CT scans.},
                    keywords = {deep learning, medical imaging, MRI},
                    doi = {10.1234/jmi.2023.007}
                }

                @article{group2_similar_title_authors,
                    title = {Deep Learning Approaches for Medical Image Analysis},
                    year = {2023},
                    author = {Robert Johnson and Sarah Williams},
                    journal = {Journal of Medical Imaging},
                    abstract = {A completely different abstract about something else entirely to test threshold behavior.},
                    keywords = {deep learning, medical imaging, MRI},
                    doi = {10.1234/jmi.2023.008}
                }

                @article{group3_no_abstract1,
                    title = {Natural Language Processing in Clinical Settings},
                    year = {2023},
                    author = {David Brown and Lisa Chen},
                    journal = {Journal of Clinical Informatics},
                    keywords = {NLP, clinical informatics, patient care},
                    doi = {10.1234/jci.2023.001}
                }

                @article{group3_no_abstract2,
                    title = {Natural Language Processing in Clinical Settings},
                    year = {2023},
                    author = {David Brown and Lisa Chen},
                    journal = {Journal of Clinical Informatics},
                    keywords = {NLP, clinical informatics, patient care},
                    doi = {10.1234/jci.2023.002}
                }

                @article{unique1,
                    title = {Blockchain Applications in Healthcare Data Management},
                    year = {2023},
                    author = {Michael Lee and Jennifer Wang},
                    journal = {Journal of Health Informatics},
                    abstract = {This paper discusses the potential of blockchain technology for secure and efficient healthcare data management.},
                    keywords = {blockchain, healthcare, data management},
                    doi = {10.1234/jhi.2023.001}
                }

                @article{unique2,
                    title = {Internet of Things in Remote Patient Monitoring},
                    year = {2023},
                    author = {Thomas Wilson and Emily Davis},
                    journal = {Journal of Telemedicine},
                    abstract = {This research explores the applications of IoT devices in remote patient monitoring systems.},
                    keywords = {IoT, remote monitoring, telemedicine},
                    doi = {10.1234/jt.2023.001}
                }
            """
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews, emptyList())

        /*
            GROUP 1: Original should match with similar title
         */
        assertTrue(result.containsKey(studyReviews[0]))
        assertEquals(1, result[studyReviews[0]]!!.size)
        assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1])) // similar title

        // Verify duplicate is marked as duplicated
        assertEquals(br.all.domain.model.study.SelectionStatus.DUPLICATED, studyReviews[1].selectionStatus)
        assertEquals(br.all.domain.model.study.ExtractionStatus.DUPLICATED, studyReviews[1].extractionStatus)

        // Verify search sources are merged
        assertTrue(studyReviews[0].searchSources.contains("Test"))
        assertEquals(1, studyReviews[0].searchSources.size)

        // Kinda different abstract should not be detected as duplicate, though It can be changed by the threshold
        assertFalse(result[studyReviews[0]]!!.contains(studyReviews[2])) // similar abstract

        // Different year should not be detected as duplicate
        assertFalse(result[studyReviews[0]]!!.contains(studyReviews[3]))

        // Different authors should not be detected as duplicate
        assertFalse(result[studyReviews[0]]!!.contains(studyReviews[4]))

        // Different abstract should not be detected as duplicate
        assertFalse(result[studyReviews[0]]!!.contains(studyReviews[5]))

        /*
            GROUP 2: Original should match with duplicate
         */
        assertTrue(result.containsKey(studyReviews[6]))
        assertEquals(1, result[studyReviews[6]]!!.size)
        assertTrue(result[studyReviews[6]]!!.contains(studyReviews[7])) // exact duplicate

        // Verify duplicate is marked as duplicated
        assertEquals(br.all.domain.model.study.SelectionStatus.DUPLICATED, studyReviews[7].selectionStatus)
        assertEquals(br.all.domain.model.study.ExtractionStatus.DUPLICATED, studyReviews[7].extractionStatus)

        // Verify search sources are merged
        assertTrue(studyReviews[6].searchSources.contains("Test"))
        assertEquals(1, studyReviews[6].searchSources.size)

        // Similar title and authors but different abstract should not be detected as duplicate
        assertFalse(result[studyReviews[6]]!!.contains(studyReviews[8]))

        /*
        Group 3: Studies with no abstracts but identical title and authors
         */
        assertTrue(result.containsKey(studyReviews[9]))
        assertEquals(1, result[studyReviews[9]]!!.size)
        assertTrue(result[studyReviews[9]]!!.contains(studyReviews[10])) // no abstract but same title/authors

        // Verify duplicate is marked as duplicated
        assertEquals(br.all.domain.model.study.SelectionStatus.DUPLICATED, studyReviews[10].selectionStatus)
        assertEquals(br.all.domain.model.study.ExtractionStatus.DUPLICATED, studyReviews[10].extractionStatus)

        // Verify search sources are merged
        assertTrue(studyReviews[9].searchSources.contains("Test"))
        assertEquals(1, studyReviews[9].searchSources.size)


        assertFalse(result.containsKey(studyReviews[11]))
        assertFalse(result.containsKey(studyReviews[12]))
    }

    @Test
    fun `should mark duplicates as duplicated and merge search sources`() {
        val source1 = mutableSetOf("Source1")
        val source2 = mutableSetOf("Source2")

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

        val study1 = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source1).first[0]
        val study2 = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source2).first[1]

        val result = sut.findDuplicates(listOf(study1, study2), emptyList())

        assertEquals(1, result.size)
        assertTrue(result.containsKey(study1))
        assertEquals(1, result[study1]!!.size)
        assertTrue(result[study1]!!.contains(study2))

        assertEquals(br.all.domain.model.study.SelectionStatus.DUPLICATED, study2.selectionStatus)
        assertEquals(br.all.domain.model.study.ExtractionStatus.DUPLICATED, study2.extractionStatus)

        assertTrue(study1.searchSources.contains("Source1"))
        assertTrue(study1.searchSources.contains("Source2"))
        assertEquals(2, study1.searchSources.size)
    }
}
