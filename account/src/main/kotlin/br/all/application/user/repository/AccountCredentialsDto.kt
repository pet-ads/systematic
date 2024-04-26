package br.all.application.user.repository

import java.util.*

data class AccountCredentialsDto(
    val id: UUID,
    val username: String,
    val password: String,
    val refreshToken: String?
)