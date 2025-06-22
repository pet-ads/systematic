package br.all.infrastructure.similarity

import br.all.domain.services.LevenshteinSimilarityCalculator
import org.apache.commons.text.similarity.LevenshteinDistance

class LevenshteinSimilarityCalculatorImpl : LevenshteinSimilarityCalculator {
    override fun similarity(text1: String, text2: String): Double {
        val levenshtein = LevenshteinDistance.getDefaultInstance()
        val distance = levenshtein.apply(text1, text2).toDouble()
        val lengthOfLargerString = maxOf(text1.length, text2.length)

        return 1.0 - (distance / lengthOfLargerString)
    }
}