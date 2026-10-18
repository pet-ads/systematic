package br.all.domain.model.reviewmode

import br.all.domain.shared.user.ResearcherId

class Replication(
    val mainReviewer: ResearcherId,
    val secondaryReviewers: Map<ResearcherId, Double> = emptyMap(),
    val noStudySharedBySecondaries: Boolean = true,
) : ReviewMode {
    val reviewers: Set<ResearcherId> = setOf(mainReviewer) + secondaryReviewers.keys

    init {
        if (noStudySharedBySecondaries) {
            require(secondaryReviewers.values.sum() <= 100.0) {
                "Secondary percentages cannot exceed 100 when studies are not shared"
            }
        }
    }
}
