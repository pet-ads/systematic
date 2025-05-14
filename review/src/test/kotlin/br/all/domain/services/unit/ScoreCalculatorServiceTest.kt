package br.all.domain.services.unit

import br.all.domain.model.study.StudyReview
import br.all.domain.services.ScoreCalculatorService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals

@Tag("UnitTest")
class ScoreCalculatorServiceTest {

    private lateinit var sut: ScoreCalculatorService
    private val testData = ScoreCalculatorServiceTestData

    @BeforeEach
    fun setup() {
        sut = ScoreCalculatorService()
    }

    @Test
    fun `should calculate maximum score when all keywords match in all fields`() {
        val studyReview = testData.fullMatchStudyReview

        val result = sut.applyScoreToManyStudyReviews(listOf(studyReview), testData.protocolKeywords)

        assertEquals(30, result.first().score)
    }

    @Test
    fun `should calculate partial score when only some keywords match`() {
        val studyReview = testData.partialMatchStudyReview

        val result = sut.applyScoreToManyStudyReviews(listOf(studyReview), testData.protocolKeywords)

        assertEquals(10, result.first().score)
    }

    @Test
    fun `should return zero score when no keywords match`() {
        val studyReview = testData.noMatchStudyReview

        val result = sut.applyScoreToManyStudyReviews(listOf(studyReview), testData.protocolKeywords)

        assertEquals(0, result.first().score)
    }

    @Test
    fun `should handle null abstract properly`() {
        val studyReview = testData.nullAbstractStudyReview

        val result = sut.applyScoreToManyStudyReviews(listOf(studyReview), testData.protocolKeywords)

        assertEquals(14, result.first().score)
    }

    @Test
    fun `should count multiple occurrences of the same keyword`() {
        val studyReview = testData.repeatedKeywordsStudyReview

        val result = sut.applyScoreToManyStudyReviews(listOf(studyReview), testData.protocolKeywords)

        assertEquals(26, result.first().score)
    }

    @Test
    fun `should process multiple study reviews correctly`() {
        val studyReviews = listOf(
            testData.fullMatchStudyReview,
            testData.noMatchStudyReview,
            testData.partialMatchStudyReview
        )

        val results = sut.applyScoreToManyStudyReviews(studyReviews, testData.protocolKeywords)

        assertAll(
            { assertEquals(3, results.size) },
            { assertEquals(30, results[0].score) },
            { assertEquals(0, results[1].score) },
            { assertEquals(10, results[2].score) }
        )
    }

    @Test
    fun `should handle empty protocol keywords`() {
        val studyReview = testData.fullMatchStudyReview

        val result = sut.applyScoreToManyStudyReviews(listOf(studyReview), emptySet())

        assertEquals(0, result.first().score)
    }

    @Test
    fun `should handle empty study reviews list`() {
        val emptyList = emptyList<StudyReview>()

        val result = sut.applyScoreToManyStudyReviews(emptyList, testData.protocolKeywords)

        assertEquals(0, result.size)
    }
}