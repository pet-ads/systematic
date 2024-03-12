package br.all.infrastructure.review

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface MongoSystematicStudyRepository: MongoRepository<SystematicStudyDocument, UUID> {
    fun findAllByCollaboratorsContaining(collaborator: UUID): List<SystematicStudyDocument>
}
