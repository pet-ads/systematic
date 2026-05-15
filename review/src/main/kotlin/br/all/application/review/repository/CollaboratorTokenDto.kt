package br.all.application.review.repository

import br.all.application.user.repository.TokenStatus
import java.time.LocalDateTime
import java.util.*

data class CollaboratorTokenDto(
    val id: UUID,
    var systematicStudyId: UUID,
    var researcherId: UUID,
    var status: TokenStatus,
    var createdAt: LocalDateTime,
    var expiration: LocalDateTime,
)
