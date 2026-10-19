package br.all.domain.model.reviewmode

import br.all.domain.shared.user.ResearcherId

class Replication(
    val mainReviewer: ResearcherId,
    val secondaryReviewers: Map<ResearcherId, Double> = emptyMap(),
    val noStudySharedBySecondaries: Boolean = true,
) : ReviewMode {
    val reviewers: Set<ResearcherId> = setOf(mainReviewer) + secondaryReviewers.keys

    override fun assign(
        availableStudies: List<Long>,
        reviewerStudies: Map<ResearcherId, Set<Long>>
    ): Map<ResearcherId, Set<Long>> {
        TODO("Not yet implemented")
    }

    init {
        require(secondaryReviewers.values.all { it in 0.0..100.0 }) {
            "Each secondary percentage must be between 0 and 100"
        }

        if (noStudySharedBySecondaries) {
            require(secondaryReviewers.values.sum() <= 100.0) {
                "Secondary percentages cannot exceed 100 when studies are not shared"
            }
        }
    }
}
