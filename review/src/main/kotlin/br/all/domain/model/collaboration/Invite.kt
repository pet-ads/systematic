package br.all.domain.model.collaboration

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.ddd.Entity
import br.all.domain.user.UserAccountId
import java.time.LocalDateTime
import java.util.*

class Invite(
    id: InviteId,
    systematicStudyId: SystematicStudyId,
    userId: UserAccountId,
    inviteDate: LocalDateTime,
    expirationDate: LocalDateTime = inviteDate.plusDays(30)
) : Entity<UUID>(id) {
    
    init{
        require(expirationDate.isAfter(inviteDate))
    }
}