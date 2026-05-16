package br.all.domain.model.review

import br.all.application.user.repository.TokenStatus
import br.all.domain.shared.ddd.Entity
import java.time.LocalDateTime
import java.util.*

class CollaboratorToken(
    id: CollaboratorTokenId,
    val systematicStudyId: UUID,
    val researcherId: UUID,
    var status: TokenStatus,
    val createdAt: LocalDateTime,
    val expiration: LocalDateTime,
) : Entity<UUID>(id) {

    init {
        if(expiration.isBefore(LocalDateTime.now())) {
            status = TokenStatus.EXPIRADO
        }
    }

    companion object
}
