package br.all.infrastructure.similarity

import br.all.domain.services.LevenshteinSimilarityCalculator
import org.apache.commons.text.similarity.LevenshteinDistance
import kotlin.math.max

class LevenshteinSimilarityCalculatorImpl : LevenshteinSimilarityCalculator {
    override fun similarity(text1: String, text2: String): Double {
        val levenshtein = LevenshteinDistance.getDefaultInstance()
        val distance = levenshtein.apply(text1, text2).toDouble()
        val lengthOfLargerString = maxOf(text1.length, text2.length)

        return 1.0 - (distance / lengthOfLargerString)
    }

    override fun similarityPercentage(text1: String, text2: String, threshold: Double): Double {
        val largerLength = max(text1.length, text2.length)

        val threshold = kotlin.math.ceil(largerLength * threshold).toInt()

        val levenshtein = LevenshteinDistance(threshold)

        val distance = levenshtein.apply(text1, text2)

        if (distance == -1) {
            return 0.0
        }

        val lengthOfLargerString = maxOf(text1.length, text2.length)

        return 1.0 - (distance.toDouble() / lengthOfLargerString.toDouble())
    }
}