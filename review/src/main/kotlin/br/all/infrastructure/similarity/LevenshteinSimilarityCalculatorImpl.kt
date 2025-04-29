package br.all.infrastructure.similarity

import br.all.domain.services.LevenshteinSimilarityCalculator
import br.all.domain.shared.utils.normalizeText
import org.apache.commons.text.similarity.LevenshteinDistance

class LevenshteinSimilarityCalculatorImpl : LevenshteinSimilarityCalculator {
    override fun similarity(text1: String, text2: String): Double {
        val normalizedText1 = normalizeText(text1)
        val normalizedText2 = normalizeText(text2)

        if (normalizedText1.isEmpty() && normalizedText2.isEmpty()) return 1.0
        if (normalizedText1.isEmpty() || normalizedText2.isEmpty()) return 0.0

        val levenshtein = LevenshteinDistance.getDefaultInstance()
        val distance = levenshtein.apply(normalizedText1, normalizedText2).toDouble()
        val lengthOfLargerString = maxOf(normalizedText1.length, normalizedText2.length)

        return 1.0 - (distance / lengthOfLargerString)
    }
}