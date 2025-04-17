package br.all.domain.services

import br.all.domain.model.study.StudyReview

class ReviewSimilarityService {
    private val titleThreshold: Double = 0.8
    private val authorsThreshold: Double = 0.8
    private val abstractThreshold: Double = 0.8

    public fun detectDuplicates(studies: List<StudyReview>): Map<StudyReview, Set<StudyReview>> {
        TODO()
    }

    private fun compareTwoStudies(study1: StudyReview, study2: StudyReview): Boolean {
        TODO()
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
}
