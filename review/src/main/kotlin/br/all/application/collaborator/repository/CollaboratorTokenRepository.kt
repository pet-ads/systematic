package br.all.application.collaborator.repository

import java.util.*

interface CollaboratorTokenRepository {
    fun saveOrUpdate(dto: CollaboratorTokenDto)

    fun findById(id : UUID) : CollaboratorTokenDto?

    fun findAllBySystematicStudyId(systematicStudyId: UUID) : List<CollaboratorTokenDto>

    fun existsById(id: UUID) : Boolean

    fun deleteById(id: UUID)
}