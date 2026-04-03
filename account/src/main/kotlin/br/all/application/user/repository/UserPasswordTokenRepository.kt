package br.all.application.user.repository

import java.util.UUID

interface UserPasswordTokenRepository {
    fun save(dto: UserPasswordTokenDto): UserPasswordTokenDto
    fun existsByEmail(email: String): Boolean
    fun deleteByToken(id: UUID)
    fun findByEmail(email: String): UserPasswordTokenDto?
    fun update(dto: UserPasswordTokenDto): UserPasswordTokenDto
    fun findByToken(token: UUID): UserPasswordTokenDto?
}