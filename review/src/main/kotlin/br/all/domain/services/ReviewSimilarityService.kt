package br.all.domain.services

import br.all.domain.model.study.StudyReview
import br.all.domain.shared.utils.normalizeText

class ReviewSimilarityService(
    private val levenshteinSimilarityCalculator: LevenshteinSimilarityCalculator,
    private val titleThreshold: Double = 0.8,
    private val abstractThreshold: Double = 0.8,
    private val authorsThreshold: Double = 0.8
) {
    fun findDuplicates(newStudies: List<StudyReview>, oldStudies: List<StudyReview>): Map<StudyReview, Set<StudyReview>> {
        val duplicatedReviewsMap = mutableMapOf<StudyReview, MutableSet<StudyReview>>()
        val remainingStudies = newStudies.toMutableList()
        remainingStudies.addAll(oldStudies)

        while (remainingStudies.isNotEmpty()) {
            remainingStudies.removeFirst().let { a ->
                val duplicatesOfA = mutableSetOf<StudyReview>()
                val iterator = remainingStudies.iterator()

                while (iterator.hasNext()) {
                    val b = iterator.next()
                    if (!sameYear(a, b)) continue

                    if (calculateTitleSimilarity(a, b) >= titleThreshold && calculateAuthorsSimilarity(a, b) >= authorsThreshold) {
                        if (calculateAbstractSimilarity(a, b) >= abstractThreshold) {
                            duplicatesOfA.add(b)
                            iterator.remove()
                        }
                    }
                }

                if (duplicatesOfA.isNotEmpty()) duplicatedReviewsMap[a] = duplicatesOfA
            }
        }

        duplicatedReviewsMap.forEach { (review, duplicates) ->
            review.markAsDuplicated(duplicates.toList())
        }

        return duplicatedReviewsMap
    }

    private fun sameYear(study1: StudyReview, study2: StudyReview) = study1.year == study2.year

    private fun calculateTitleSimilarity(study1: StudyReview, study2: StudyReview): Double {
        val normalizedText1 = normalizeText(study1.title)
        val normalizedText2 = normalizeText(study2.title)

        if (normalizedText1.isEmpty() && normalizedText2.isEmpty()) return 1.0
        if (normalizedText1.isEmpty() || normalizedText2.isEmpty()) return 0.0

        return levenshteinSimilarityCalculator.similarity(normalizedText1, normalizedText2)
    }

    private fun calculateAbstractSimilarity(study1: StudyReview, study2: StudyReview): Double {
        val normalizedText1 = normalizeText(study1.abstract ?: "")
        val normalizedText2 = normalizeText(study2.abstract ?: "")

        if (normalizedText1.isEmpty() && normalizedText2.isEmpty()) return 1.0
        if (normalizedText1.isEmpty() || normalizedText2.isEmpty()) return 0.0

        return levenshteinSimilarityCalculator.similarity(normalizedText1, normalizedText2)
    }

    private fun calculateAuthorsSimilarity(study1: StudyReview, study2: StudyReview): Double {
        val normalizedText1 = normalizeText(study1.authors)
        val normalizedText2 = normalizeText(study2.authors)

        if (normalizedText1.isEmpty() && normalizedText2.isEmpty()) return 1.0
        if (normalizedText1.isEmpty() || normalizedText2.isEmpty()) return 0.0

        return levenshteinSimilarityCalculator.similarity(normalizedText1, normalizedText2)
    }
}
