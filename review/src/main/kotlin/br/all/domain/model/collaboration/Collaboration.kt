package br.all.domain.model.collaboration

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.ddd.Entity
import br.all.domain.user.UserAccountId
import java.util.UUID

class Collaboration(
    id: CollaborationId,
    title: String,
    systematicStudyId: SystematicStudyId,
    userId: UserAccountId,
    status: CollaborationStatus,
    permissions: Set<CollaborationPermission> = emptySet()
    ): Entity<UUID>(id) {
}