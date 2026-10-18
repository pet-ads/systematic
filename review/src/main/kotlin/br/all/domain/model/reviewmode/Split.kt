package br.all.domain.model.reviewmode

import br.all.domain.shared.user.ResearcherId

class Split(
    val reviewers: List<ResearcherId> = emptyList(),
    val reviewPerStudy: Int,
) : ReviewMode {
    constructor(reviewPerStudy: Int) : this(emptyList(), reviewPerStudy)

    init {
        require(reviewers.isNotEmpty()) { "Split needs at least one reviewer" }
        require(reviewPerStudy in 1..reviewers.size) {
            "Review per study must be between 1 and the number of reviewers"
        }
    }

    fun split(
        availableStudies: List<Long>,
        reviewerStudies: Map<ResearcherId, Set<Long>>,
    ): Map<ResearcherId, Set<Long>> {
        require(availableStudies.isNotEmpty()) { "There must be at least one available study to split" }
        return emptyMap()
    }
}
