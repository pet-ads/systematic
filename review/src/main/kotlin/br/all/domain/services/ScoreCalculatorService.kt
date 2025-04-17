package br.all.domain.services

import br.all.domain.model.study.StudyReview

class ScoreCalculatorService(
    private val protocolKeywords: Set<String>?
) {
    private fun calculateScore(title: String, abstract: String?, authorsKeywords: Set<String>): Int {
        var score = 0

        if (protocolKeywords != null) {
            for (keyword in protocolKeywords) {
                val regex = Regex("\\b$keyword\\b", RegexOption.IGNORE_CASE)

                if (regex.containsMatchIn(title)) score += 5
                if (abstract?.let { regex.containsMatchIn(it) } == true) score += 3
                if (authorsKeywords.any { regex.containsMatchIn(it) }) score += 2
            }
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