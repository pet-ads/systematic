package br.all.application.user.repository

import java.util.UUID

interface ConfirmAccountTokenRepository {
    fun save(dto: ConfirmAccountTokenDto): ConfirmAccountTokenDto
    fun findById(token: UUID): ConfirmAccountTokenDto?
    fun deleteByToken(token: UUID)
    fun update(dto: ConfirmAccountTokenDto)
    fun findByUserId(userId: UUID): ConfirmAccountTokenDto?
}