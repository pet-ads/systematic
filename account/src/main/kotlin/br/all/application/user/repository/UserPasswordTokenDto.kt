package br.all.application.user.repository

import br.all.infrastructure.user.TokenStatus
import java.time.LocalDateTime
import java.util.UUID

data class UserPasswordTokenDto(
    val id: UUID,
    val userId: UUID,
    val email: String,
    val token: String,
    val status: TokenStatus,
    val createdAt: LocalDateTime,
    val expiration: LocalDateTime,
    val language: String
)