package br.all.domain.services

import br.all.domain.model.protocol.Protocol
import br.all.domain.model.study.StudyReview

class ScoreCalculatorService(
    val protocol: Protocol
) {
    private fun calculateScore(studyReview: StudyReview): Int {
        val keywords = protocol.keywords
        var score = 0

        for (keyword in keywords) {
            val regex = Regex("\\b$keyword\\b", RegexOption.IGNORE_CASE)

            if (regex.containsMatchIn(studyReview.title)) score += 5
            if (studyReview.abstract?.let { regex.containsMatchIn(it) } == true) score += 3
            if (studyReview.keywords.any { regex.containsMatchIn(it) }) score += 2
        }

        return score
    }
}