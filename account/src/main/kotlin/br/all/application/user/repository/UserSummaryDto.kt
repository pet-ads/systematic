package br.all.application.user.repository

import java.util.UUID

data class UserSummaryDto(
    val id: UUID,
    val username: String,
    val email: String
)
