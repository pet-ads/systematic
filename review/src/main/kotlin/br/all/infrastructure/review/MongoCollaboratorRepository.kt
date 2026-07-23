package br.all.infrastructure.review

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface MongoCollaboratorRepository: MongoRepository<CollaboratorDocument, UUID>{
    fun existsByResearcherIdAndSystematicStudyId(researcherId: UUID, systematicStudyId: UUID): Boolean

    fun findAllBySystematicStudyId(systematicStudyId: UUID): List<CollaboratorDocument>

    fun findByResearcherIdAndSystematicStudyId(researcherId: UUID, systematicStudyId: UUID): CollaboratorDocument?

    fun deleteByResearcherIdAndSystematicStudyId(researcherId: UUID, systematicStudyId: UUID)
}
