package br.all.domain.services

import br.all.domain.model.study.StudyReview
import br.all.domain.shared.utils.normalizeText
import org.springframework.stereotype.Service

@Service
class ScoreCalculatorService {
    private fun calculateScore(title: String, abstract: String?, authorsKeywords: Set<String>, protocolKeywords: Set<String>): Int {
        var score = 0

        val normalizedTitle = normalizeText(title)
        val normalizedAbstract = abstract?.let { normalizeText(it) } ?: ""
        val normalizedAuthorsKeywords = authorsKeywords.map { normalizeText(it) }.toSet()

        for (keyword in protocolKeywords) {
            val normalizedKeyword = normalizeText(keyword)
            val regex = Regex("\\b$normalizedKeyword\\b", RegexOption.IGNORE_CASE)

            score += regex.findAll(normalizedTitle).count() * 5
            score += regex.findAll(normalizedAbstract).count() * 3
            if (normalizedAuthorsKeywords.any { regex.containsMatchIn(it) }) score += 2
        }

        return score
    }

    private fun applyScoreToStudyReview(studyReview: StudyReview, protocolKeywords: Set<String>): StudyReview {
        val score = calculateScore(studyReview.title, studyReview.abstract, studyReview.keywords, protocolKeywords)
        return studyReview.apply { this.score = score }
    }

    fun applyScoreToManyStudyReviews(studyReviews: List<StudyReview>, protocolKeywords: Set<String>): List<StudyReview> {
        return studyReviews.map { applyScoreToStudyReview(it, protocolKeywords) }
    }
}