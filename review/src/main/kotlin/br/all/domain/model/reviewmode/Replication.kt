package br.all.domain.model.reviewmode

import br.all.domain.shared.user.ResearcherId

class Replication(
    val mainReviewer: ResearcherId,
    val secondaryReviewers: Map<ResearcherId, Double> = emptyMap(),
    val noStudySharedBySecondaries: Boolean = true,
) : ReviewMode {
    val reviewers: Set<ResearcherId> = setOf(mainReviewer) + secondaryReviewers.keys
}
