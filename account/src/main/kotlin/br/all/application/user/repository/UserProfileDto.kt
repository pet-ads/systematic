package br.all.application.user.repository

import java.util.UUID

data class UserProfileDto(
    val id: UUID,
    val name: String,
    val email: String,
    val country: String,
    val affiliation: String,
)
