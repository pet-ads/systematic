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

                    if (!areLengthsCompatibleBySize(a.title, b.title, 0.50) && !areLengthsCompatibleBySize(a.authors, b.authors, 0.50)) continue

                    if( !a.abstract.isNullOrEmpty() && !b.abstract.isNullOrEmpty() ) {
                        if (!areLengthsCompatibleBySize(a.abstract.trim(), b.abstract.trim())) continue
                    }

                    if (calculateTitleSimilarity(a, b, titleThreshold) >= titleThreshold && calculateAuthorsSimilarity(a, b, authorsThreshold) >= authorsThreshold) {
                        if (calculateAbstractSimilarity(a, b, abstractThreshold) >= abstractThreshold) {
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

    private fun calculateTitleSimilarity(study1: StudyReview, study2: StudyReview, threshold: Double): Double {
        val normalizedText1 = normalizeText(study1.title)
        val normalizedText2 = normalizeText(study2.title)

        val basicCompared = basicComparingFilter(normalizedText1, normalizedText2)

        if (basicCompared != null) return basicCompared

        return levenshteinSimilarityCalculator.similarityPercentage(normalizedText1, normalizedText2, 1.0 - threshold)
    }

    private fun calculateAbstractSimilarity(study1: StudyReview, study2: StudyReview, threshold: Double): Double {
        val normalizedText1 = normalizeText(study1.abstract ?: "")
        val normalizedText2 = normalizeText(study2.abstract ?: "")

        val basicCompared = basicComparingFilter(normalizedText1, normalizedText2)

        if (basicCompared != null) return basicCompared

        return levenshteinSimilarityCalculator.similarityPercentage(normalizedText1, normalizedText2, 1.0 - threshold)
    }

    private fun calculateAuthorsSimilarity(study1: StudyReview, study2: StudyReview, threshold: Double): Double {
        val normalizedText1 = normalizeText(study1.authors)
        val normalizedText2 = normalizeText(study2.authors)

        val basicCompared = basicComparingFilter(normalizedText1, normalizedText2)

        if (basicCompared != null) return basicCompared

        return levenshteinSimilarityCalculator.similarityPercentage(normalizedText1, normalizedText2, 1.0 - threshold)
    }

    private fun basicComparingFilter(text1: String, text2: String): Double? {
        if (text1 == text2) return 1.0
        if (text1.isEmpty() || text2.isEmpty()) return 0.0

        return null
    }

    private fun areLengthsCompatibleBySize(
        a: String,
        b: String,
        maxDifferencePercent: Double = 0.40
    ): Boolean {
        val lengthA = a.length
        val lengthB = b.length

        val bigger = maxOf(lengthA, lengthB)
        val smaller = minOf(lengthA, lengthB)

        val difference = bigger - smaller

        return difference.toDouble() / bigger <= maxDifferencePercent
    }
}
