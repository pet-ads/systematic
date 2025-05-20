package br.all.domain.services

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.domain.model.study.ExtractionStatus
import br.all.domain.model.study.SelectionStatus
import br.all.infrastructure.similarity.LevenshteinSimilarityCalculatorImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.assertAll
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Tag("IntegrationTest")
class ReviewSimilarityServiceTest {

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
        val bibtex = ReviewSimilarityTestData.testInputs["high similarity"]!!
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews, emptyList())

        assertAll(
            { assertEquals(1, result.size) },
            { assertTrue(result.containsKey(studyReviews[0])) },
            { assertEquals(1, result[studyReviews[0]]!!.size) },
            { assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1])) },
            { assertEquals(SelectionStatus.DUPLICATED, studyReviews[1].selectionStatus) },
            { assertEquals(ExtractionStatus.DUPLICATED, studyReviews[1].extractionStatus) },
            { assertTrue(studyReviews[0].searchSources.contains("Test")) },
            { assertEquals(1, studyReviews[0].searchSources.size) }
        )
    }

    @Test
    fun `should detect duplicates with identical content`() {
        val bibtex = ReviewSimilarityTestData.testInputs["identical content"]!!
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews, emptyList())

        assertAll(
            { assertEquals(1, result.size) },
            { assertTrue(result.containsKey(studyReviews[0])) },
            { assertEquals(1, result[studyReviews[0]]!!.size) },
            { assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1])) },
            { assertEquals(SelectionStatus.DUPLICATED, studyReviews[1].selectionStatus) },
            { assertEquals(ExtractionStatus.DUPLICATED, studyReviews[1].extractionStatus) },
            { assertTrue(studyReviews[0].searchSources.contains("Test")) },
            { assertEquals(1, studyReviews[0].searchSources.size) }
        )
    }

    @Test
    fun `should not detect duplicates when similarity is below threshold`() {
        val bibtex = ReviewSimilarityTestData.testInputs["below threshold"]!!
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews, emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should handle study reviews with null abstracts`() {
        val bibtex = ReviewSimilarityTestData.testInputs["null abstracts"]!!
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews, emptyList())

        assertAll(
            { assertEquals(1, result.size) },
            { assertTrue(result.containsKey(studyReviews[0])) },
            { assertEquals(1, result[studyReviews[0]]!!.size) },
            { assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1])) },
            { assertEquals(SelectionStatus.DUPLICATED, studyReviews[1].selectionStatus) },
            { assertEquals(ExtractionStatus.DUPLICATED, studyReviews[1].extractionStatus) },
            { assertTrue(studyReviews[0].searchSources.contains("Test")) },
            { assertEquals(1, studyReviews[0].searchSources.size) }
        )
    }

    @Test
    fun `should handle empty list of study reviews`() {
        val result = sut.findDuplicates(emptyList(), emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should handle list with only one study review`() {
        val bibtex = ReviewSimilarityTestData.testInputs["single study"]!!
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews, emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should detect multiple groups of duplicates`() {
        val bibtex = ReviewSimilarityTestData.testInputs["multiple groups"]!!
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews, emptyList())

        assertAll(
            // GROUP 1
            { assertTrue(result.containsKey(studyReviews[0])) },
            { assertEquals(1, result[studyReviews[0]]!!.size) },
            { assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1])) },
            { assertEquals(SelectionStatus.DUPLICATED, studyReviews[1].selectionStatus) },
            { assertEquals(ExtractionStatus.DUPLICATED, studyReviews[1].extractionStatus) },
            { assertTrue(studyReviews[0].searchSources.contains("Test")) },
            { assertEquals(1, studyReviews[0].searchSources.size) },

            // GROUP 2
            { assertTrue(result.containsKey(studyReviews[2])) },
            { assertEquals(1, result[studyReviews[2]]!!.size) },
            { assertTrue(result[studyReviews[2]]!!.contains(studyReviews[3])) },
            { assertEquals(SelectionStatus.DUPLICATED, studyReviews[3].selectionStatus) },
            { assertEquals(ExtractionStatus.DUPLICATED, studyReviews[3].extractionStatus) },
            { assertTrue(studyReviews[2].searchSources.contains("Test")) },
            { assertEquals(1, studyReviews[2].searchSources.size) },

            // Other assertions
            { assertFalse(result.containsKey(studyReviews[4])) },
            { assertEquals(2, result.size) }
        )
    }

    @Test
    fun `should skip levenshtein calculation when years are not the same`() {
        val bibtex = ReviewSimilarityTestData.testInputs["different years"]!!
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews,emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun `testing a lot of combination of possibilities at the same time`() {
        val bibtex = ReviewSimilarityTestData.testInputs["multiple combinations"]!!
        val studyReviews = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source).first
        val result = sut.findDuplicates(studyReviews, emptyList())

        assertAll(
            // GROUP 1
            { assertTrue(result.containsKey(studyReviews[0])) },
            { assertEquals(1, result[studyReviews[0]]!!.size) },
            { assertTrue(result[studyReviews[0]]!!.contains(studyReviews[1])) },
            { assertEquals(SelectionStatus.DUPLICATED, studyReviews[1].selectionStatus) },
            { assertEquals(ExtractionStatus.DUPLICATED, studyReviews[1].extractionStatus) },
            { assertTrue(studyReviews[0].searchSources.contains("Test")) },
            { assertEquals(1, studyReviews[0].searchSources.size) },
            { assertFalse(result[studyReviews[0]]!!.contains(studyReviews[2])) },
            { assertFalse(result[studyReviews[0]]!!.contains(studyReviews[3])) },
            { assertFalse(result[studyReviews[0]]!!.contains(studyReviews[4])) },
            { assertFalse(result[studyReviews[0]]!!.contains(studyReviews[5])) },

            // GROUP 2
            { assertTrue(result.containsKey(studyReviews[6])) },
            { assertEquals(1, result[studyReviews[6]]!!.size) },
            { assertTrue(result[studyReviews[6]]!!.contains(studyReviews[7])) },
            { assertEquals(SelectionStatus.DUPLICATED, studyReviews[7].selectionStatus) },
            { assertEquals(ExtractionStatus.DUPLICATED, studyReviews[7].extractionStatus) },
            { assertTrue(studyReviews[6].searchSources.contains("Test")) },
            { assertEquals(1, studyReviews[6].searchSources.size) },
            { assertFalse(result[studyReviews[6]]!!.contains(studyReviews[8])) },

            // GROUP 3
            { assertTrue(result.containsKey(studyReviews[9])) },
            { assertEquals(1, result[studyReviews[9]]!!.size) },
            { assertTrue(result[studyReviews[9]]!!.contains(studyReviews[10])) },
            { assertEquals(SelectionStatus.DUPLICATED, studyReviews[10].selectionStatus) },
            { assertEquals(ExtractionStatus.DUPLICATED, studyReviews[10].extractionStatus) },
            { assertTrue(studyReviews[9].searchSources.contains("Test")) },
            { assertEquals(1, studyReviews[9].searchSources.size) },

            // Other assertions
            { assertFalse(result.containsKey(studyReviews[11])) },
            { assertFalse(result.containsKey(studyReviews[12])) }
        )
    }

    @Test
    fun `should mark duplicates as duplicated and merge search sources`() {
        val source1 = mutableSetOf("Source1")
        val source2 = mutableSetOf("Source2")

        val bibtex = ReviewSimilarityTestData.testInputs["different sources"]!!

        val study1 = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source1).first[0]
        val study2 = converter.convertManyToStudyReview(systematicStudyId, searchSessionId, bibtex, source2).first[1]

        val result = sut.findDuplicates(listOf(study1, study2), emptyList())

        assertAll(
            { assertEquals(1, result.size) },
            { assertTrue(result.containsKey(study1)) },
            { assertEquals(1, result[study1]!!.size) },
            { assertTrue(result[study1]!!.contains(study2)) },
            { assertEquals(SelectionStatus.DUPLICATED, study2.selectionStatus) },
            { assertEquals(ExtractionStatus.DUPLICATED, study2.extractionStatus) },
            { assertTrue(study1.searchSources.contains("Source1")) },
            { assertTrue(study1.searchSources.contains("Source2")) },
            { assertEquals(2, study1.searchSources.size) }
        )
    }
}