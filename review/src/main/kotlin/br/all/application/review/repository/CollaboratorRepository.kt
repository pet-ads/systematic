package br.all.application.review.repository

import java.util.*

interface CollaboratorRepository {
    fun saveOrUpdate(dto: CollaboratorDto)

    fun findById(id : UUID) : CollaboratorDto?

    fun existsById(id: UUID) : Boolean

    fun existsByResearcherIdAndSystematicStudyId(id: UUID, systematicStudyId: UUID): Boolean
}