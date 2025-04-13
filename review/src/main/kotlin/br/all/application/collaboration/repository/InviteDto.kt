package br.all.application.collaboration.repository

import java.time.LocalDateTime
import java.util.*

class InviteDto (
    val id: UUID,
    val systematicStudyId: UUID,
    val userId: UUID,
    val inviteDate: LocalDateTime,
    val expirationDate: LocalDateTime,
    val permissions: Set<String>
)