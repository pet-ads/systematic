package br.all.application.user.repository

import java.util.UUID

interface UserPasswordTokenRepository {
    fun save(dto: UserPasswordTokenDto): UserPasswordTokenDto
    fun existsByEmail(email: String): Boolean
    fun deleteById(id: UUID)
    fun findByEmail(email: String): UserPasswordTokenDto?
}