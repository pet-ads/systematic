package br.all.domain.services

import br.all.domain.services.ScoreCalculatorServiceData.baseStudyReview
import br.all.domain.services.ScoreCalculatorServiceData.noMatchStudyReview
import br.all.domain.services.ScoreCalculatorServiceData.nullAbstractStudyReview
import br.all.domain.services.ScoreCalculatorServiceData.partialMatchStudyReview
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ScoreCalculatorServiceTest {

    private lateinit var sut: ScoreCalculatorService

    @BeforeEach
    fun setup() {
        val protocolKeywords = setOf("dengue", "mosquito", "países tropicais")
        sut = ScoreCalculatorService(protocolKeywords)
    }

    @Test
    fun `should assign maximum score when all keywords match in all fields`() {
        val result = sut.applyScoreToManyStudyReviews(listOf(baseStudyReview)).first()

        assertEquals(25, result.score)
    }

    @Test
    fun `should assign zero score when none of the keywords match`() {
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
        assertEquals(3, results.size)
        assertEquals(25, results[0].score)
        assertEquals(0, results[1].score)
        assertEquals(10, results[2].score)
    }
}
