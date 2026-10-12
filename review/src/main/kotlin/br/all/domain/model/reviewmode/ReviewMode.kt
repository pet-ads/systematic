package br.all.domain.model.reviewmode

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.ddd.Entity
import java.util.UUID

sealed class ReviewMode(
    val systematicStudyId: SystematicStudyId
) : Entity<UUID>(systematicStudyId)
