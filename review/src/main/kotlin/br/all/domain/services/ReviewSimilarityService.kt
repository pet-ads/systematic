package br.all.domain.services

import br.all.domain.model.study.StudyReview
import org.apache.commons.text.similarity.LevenshteinDistance
import java.text.Normalizer

class ReviewSimilarityService {
    private val titleThreshold: Double = 0.9
    private val authorsThreshold: Double = 0.9
    private val abstractThreshold: Double = 0.9

    public fun detectDuplicates(studies: List<StudyReview>): Map<StudyReview, Set<StudyReview>> {
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
        TODO()
    }

    private fun calculateAuthorsSimilarity(study1: StudyReview, study2: StudyReview): Double {
        TODO()
    }

    private fun calculateAbstractSimilarity(study1: StudyReview, study2: StudyReview): Double {
        TODO()
    }

    private fun normalizeText(text: String): String {
        TODO()
    }

    private fun similarity(text1: String, text2: String): Double {
        TODO()
    }
}
