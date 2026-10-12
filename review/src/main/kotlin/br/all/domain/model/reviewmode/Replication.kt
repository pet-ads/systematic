package br.all.domain.model.reviewmode

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.user.ResearcherId

class Replication(
    systematicStudyId: SystematicStudyId,
) : ReviewMode(systematicStudyId)
