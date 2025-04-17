package br.all.domain.services

import br.all.domain.services.ScoreCalculatorServiceTestData.baseStudyReview
import br.all.domain.services.ScoreCalculatorServiceTestData.noMatchStudyReview
import br.all.domain.services.ScoreCalculatorServiceTestData.nullAbstractStudyReview
import br.all.domain.services.ScoreCalculatorServiceTestData.partialMatchStudyReview
import junit.framework.TestCase.assertNull
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ScoreCalculatorServiceTest {

    private lateinit var sut: ScoreCalculatorService

    @BeforeEach
    fun setup() {
        val protocolKeywords = setOf("dengue", "mosquito", "países tropicais")
        sut = ScoreCalculatorService(protocolKeywords)
    }

    @Test
    fun `should return zero if protocol has no keywords`() {
        val protocolWithNoKeywords = setOf("")
        val scoreCalculatorService = ScoreCalculatorService(protocolWithNoKeywords)
        val result = scoreCalculatorService.applyScoreToManyStudyReviews(listOf(baseStudyReview)).first()
        assertEquals(0, result.score)
    }

    @Test
    fun `should assign score to matches in all fields`() {
        val result = sut.applyScoreToManyStudyReviews(listOf(baseStudyReview)).first()

        assertEquals(25, result.score)
    }

    @Test
    fun `should assign zero when none of the keywords match`() {
        val result = sut.applyScoreToManyStudyReviews(listOf(noMatchStudyReview)).first()
        assertEquals(0, result.score)
    }

    @Test
    fun `should assign partial score when only some keywords match`() {
        val result = sut.applyScoreToManyStudyReviews(listOf(partialMatchStudyReview)).first()
        assertEquals(10, result.score)
    }

    @Test
    fun `should handle study review with null abstract`() {
        val result = sut.applyScoreToManyStudyReviews(listOf(nullAbstractStudyReview)).first()
        assertEquals(16, result.score)
    }

    @Test
    fun `should return list with correct size and scores`() {
        val input = listOf(baseStudyReview, noMatchStudyReview, partialMatchStudyReview)
        val results = sut.applyScoreToManyStudyReviews(input)
        assertAll(
            {assertEquals(3, results.size)},
            {assertEquals(25, results[0].score)},
            {assertEquals(0, results[1].score)},
            {assertEquals(10, results[2].score)}
        )
    }
}
