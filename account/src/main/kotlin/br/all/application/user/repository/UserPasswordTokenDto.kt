package br.all.application.user.repository

import java.time.LocalDateTime
import java.util.UUID

data class UserPasswordTokenDto(
    val id: UUID,
    val tokenId: UUID,
    val status: String,
    val hour: LocalDateTime,
    val language: String
)