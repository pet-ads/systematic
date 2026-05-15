package br.all.application.review.repository

import java.util.*

interface CollaboratorTokenRepository {
    fun saveOrUpdate(dto: CollaboratorTokenDto)

    fun findById(id : UUID) : CollaboratorTokenDto?

    fun existsById(id: UUID) : Boolean
}