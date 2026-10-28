package br.all.domain.model.reviewmode

import br.all.domain.shared.user.ResearcherId

sealed interface ReviewMode{
    val acceptMajority: Boolean

    fun assign(
        availableStudies: List<Long>,
        reviewerStudies: Map<ResearcherId, Set<Long>>,
    ): Map<ResearcherId, Set<Long>>
}
