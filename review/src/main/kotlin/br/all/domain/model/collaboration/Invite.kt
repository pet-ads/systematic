package br.all.domain.model.collaboration

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.user.ResearcherId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification
import java.time.LocalDateTime
import java.util.*

class Invite(
    id: InviteId,
    val systematicStudyId: SystematicStudyId,
    val userId: ResearcherId,
    val inviteDate: LocalDateTime,
    val expirationDate: LocalDateTime = inviteDate.plusDays(30)
) : Entity<UUID>(id) {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    fun validate() = Notification().also {
        if (inviteDate.isAfter(expirationDate)) 
            it.addError("Invite date cannot be after expiration date")
    }
}