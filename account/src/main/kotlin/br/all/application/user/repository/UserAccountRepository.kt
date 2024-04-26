package br.all.application.user.repository

import java.util.UUID

interface UserAccountRepository {
    fun save(dto: UserAccountDto)
    fun loadCredentialsByUsername(username: String): AccountCredentialsDto?
    fun loadCredentialsByToken(token: String): AccountCredentialsDto?
    fun loadCredentialsById(id: UUID): AccountCredentialsDto?
    fun updateRefreshToken(userId: UUID, token: String)
    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean
}