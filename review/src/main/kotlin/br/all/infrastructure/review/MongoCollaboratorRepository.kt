package br.all.infrastructure.review

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface MongoCollaboratorRepository: MongoRepository<CollaboratorDocument, UUID>{
    fun existsByResearcherIdAndSystematicStudyId(researcherId: UUID, systematicStudyId: UUID): Boolean
}
