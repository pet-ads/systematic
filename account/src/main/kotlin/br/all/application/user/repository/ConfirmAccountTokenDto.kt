package br.all.application.user.repository

import java.time.LocalDateTime
import java.util.UUID

data class ConfirmAccountTokenDto(
    val token: UUID,
    val userId: UUID,
    val status: TokenStatus,
    val createdAt: LocalDateTime,
    val expiration: LocalDateTime,
)