package br.all.domain.services

import br.all.application.protocol.repository.ProtocolDto
import br.all.domain.model.study.StudyReview

class ScoreCalculatorService(
    val protocol: ProtocolDto?
) {
    private fun calculateScore(title: String, abstract: String?, authorsKeywords: Set<String>): Int {
        val keywords = protocol?.keywords ?: emptySet()
        var score = 0

        for (keyword in keywords) {
            val regex = Regex("\\b$keyword\\b", RegexOption.IGNORE_CASE)

            if (regex.containsMatchIn(title)) score += 5
            if (abstract?.let { regex.containsMatchIn(it) } == true) score += 3
            if (authorsKeywords.any { regex.containsMatchIn(it) }) score += 2
        }

        return score
    }

    private fun applyScoreToStudyReview(studyReview: StudyReview): StudyReview {
        val score = calculateScore(studyReview.title, studyReview.abstract, studyReview.keywords)
        return studyReview.apply { this.score = score }
    }

    fun applyScoreToManyStudyReviews(studyReviews: List<StudyReview>): List<StudyReview> {
        return studyReviews.map { applyScoreToStudyReview(it) }
    }
}