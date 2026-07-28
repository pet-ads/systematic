package br.all.application.collaborator.repository

import java.util.*

interface CollaboratorRepository {
    fun saveOrUpdate(dto: CollaboratorDto)

    fun findById(id : UUID) : CollaboratorDto?

    fun findAll(systematicStudyId: UUID) : List<CollaboratorDto>

    fun existsById(id: UUID) : Boolean

    fun existsByResearcherIdAndSystematicStudyId(id: UUID, systematicStudyId: UUID): Boolean

    fun findByResearcherIdAndSystematicStudyId(id: UUID, systematicStudyId: UUID): CollaboratorDto?

    fun delete(dto: CollaboratorDto)

    fun deleteByResearcherIdAndSystematicStudyId(id: UUID, systematicStudyId: UUID)
}