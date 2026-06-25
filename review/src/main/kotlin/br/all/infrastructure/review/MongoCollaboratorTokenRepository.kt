package br.all.infrastructure.review

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface MongoCollaboratorTokenRepository: MongoRepository<CollaboratorTokenDocument, UUID>{
    fun findAllBySystematicStudyId(
        systematicStudyId: UUID
    ): List<CollaboratorTokenDocument>
}
