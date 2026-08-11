package br.all.application.collaborator.repository

import br.all.application.user.repository.TokenStatus
import java.time.LocalDateTime
import java.util.*

data class CollaboratorTokenDto(
    val id: UUID,
    var systematicStudyId: UUID,
    var researcherId: UUID,
    var email: String,
    var username: String,
    var status: TokenStatus,
    var createdAt: LocalDateTime,
    var expiration: LocalDateTime,
)
