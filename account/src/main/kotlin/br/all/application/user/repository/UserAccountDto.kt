package br.all.application.user.repository

import java.time.LocalDateTime
import java.util.*

data class UserAccountDto(
    val id: UUID,
    val username: String,
    val password: String,
    val email: String,
    val country: String,
    val affiliation: String,
    val createdAt: LocalDateTime,
    val authorities: Set<String>,
    val refreshToken: String? = null,
    val isAccountNonExpired : Boolean,
    val isAccountNonLocked : Boolean,
    val isCredentialsNonExpired : Boolean,
    val isEnabled : Boolean,
)
