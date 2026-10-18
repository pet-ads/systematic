package br.all.domain.model.reviewmode

import br.all.domain.shared.user.ResearcherId

class Split(
    val reviewers: List<ResearcherId> = emptyList(),
    val reviewPerStudy: Int,
) : ReviewMode {
    constructor(reviewPerStudy: Int) : this(emptyList(), reviewPerStudy)

    init {
        require(reviewPerStudy > 0) { "Review per study must be greater than zero" }
    }
}
