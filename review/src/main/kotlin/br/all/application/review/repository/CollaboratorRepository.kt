package br.all.application.review.repository

import java.util.*

interface CollaboratorRepository {
    fun saveOrUpdate(dto: CollaboratorDto): CollaboratorDto

    fun findById(id : UUID) : CollaboratorDto?

    fun existsById(id: UUID) : Boolean
}