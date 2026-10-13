package br.all.domain.model.reviewmode

import br.all.domain.shared.user.ResearcherId

class Replication(val mainReviewer: ResearcherId) : ReviewMode {
    val reviewers: Set<ResearcherId> = setOf(mainReviewer)
}