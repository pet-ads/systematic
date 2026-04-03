package br.all.application.user.repository

import br.all.application.user.repository.TokenStatus
import java.time.LocalDateTime
import java.util.UUID

data class UserPasswordTokenDto(
    val token: UUID,
    val userId: UUID,
    val email: String,
    val status: TokenStatus,
    val createdAt: LocalDateTime,
    val expiration: LocalDateTime,
    val language: String
)