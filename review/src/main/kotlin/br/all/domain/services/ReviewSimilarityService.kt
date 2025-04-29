package br.all.domain.services

import br.all.domain.model.study.StudyReview

class ReviewSimilarityService(
    private val levenshteinSimilarityCalculator: LevenshteinSimilarityCalculator
) {
    private val titleThreshold: Double = 0.8
    private val abstractThreshold: Double = 0.8
    private val authorsThreshold: Double = 0.8

    fun findDuplicates(studies: List<StudyReview>): Map<StudyReview, Set<StudyReview>> {
        val duplicatedReviewsMap = mutableMapOf<StudyReview, MutableSet<StudyReview>>()
        val remainingStudies = studies.toMutableList()

        while (remainingStudies.isNotEmpty()) {
            remainingStudies.removeFirst().let { a ->
                val duplicatesOfA = mutableSetOf<StudyReview>()
                val iterator = remainingStudies.iterator()

                while (iterator.hasNext()) {
                    val b = iterator.next()
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

        return duplicatedReviewsMap
    }

    private fun calculateTitleSimilarity(study1: StudyReview, study2: StudyReview): Double {
        return levenshteinSimilarityCalculator.similarity(study1.title, study2.title)
    }

    private fun calculateAbstractSimilarity(study1: StudyReview, study2: StudyReview): Double {
        return levenshteinSimilarityCalculator.similarity(study1.abstract ?: "", study2.abstract ?: "")
    }

    private fun calculateAuthorsSimilarity(study1: StudyReview, study2: StudyReview): Double {
        return levenshteinSimilarityCalculator.similarity(study1.authors, study2.authors)
    }
}
