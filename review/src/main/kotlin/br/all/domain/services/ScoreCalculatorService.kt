package br.all.domain.services

import br.all.domain.model.study.StudyReview
import br.all.domain.shared.utils.normalizeText

class ScoreCalculatorService(
    private val protocolKeywords: Set<String>?
) {
    private fun calculateScore(title: String, abstract: String?, authorsKeywords: Set<String>): Int {
        var score = 0

        val normalizedTitle = normalizeText(title)
        val normalizedAbstract = abstract?.let { normalizeText(it) } ?: ""
        val normalizedAuthorsKeywords = authorsKeywords.map { normalizeText(it) }.toSet()

        if (protocolKeywords != null) {
            for (keyword in protocolKeywords) {
                val normalizedKeyword = normalizeText(keyword)
                val regex = Regex("\\b$normalizedKeyword\\b", RegexOption.IGNORE_CASE)

                if (regex.containsMatchIn(normalizedTitle)) score += 5
                if (regex.containsMatchIn(normalizedAbstract)) score += 3
                if (normalizedAuthorsKeywords.any { regex.containsMatchIn(it) }) score += 2
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