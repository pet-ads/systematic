package br.all.infrastructure.collaborator

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface MongoCollaboratorTokenRepository: MongoRepository<CollaboratorTokenDocument, UUID>{
    fun findAllBySystematicStudyId(
        systematicStudyId: UUID
    ): List<CollaboratorTokenDocument>
}
