package br.all.domain.services

import br.all.domain.model.protocol.Protocol
import br.all.domain.model.study.StudyReview

class ScoreCalculatorService(
    val protocol: Protocol
) {
    private fun calculateScore(title: String, abstract: String?, authorsKeywords: Set<String>): Int {
        val keywords = protocol.keywords
        var score = 0

        for (keyword in keywords) {
            val regex = Regex("\\b$keyword\\b", RegexOption.IGNORE_CASE)

            if (regex.containsMatchIn(title)) score += 5
            if (abstract?.let { regex.containsMatchIn(it) } == true) score += 3
            if (authorsKeywords.any { regex.containsMatchIn(it) }) score += 2
        }

        return score
    }
}