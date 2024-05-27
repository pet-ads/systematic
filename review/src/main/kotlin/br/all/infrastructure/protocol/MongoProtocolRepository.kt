package br.all.infrastructure.protocol

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface MongoProtocolRepository: MongoRepository<ProtocolDocument, UUID>{
    fun findBySystematicStudyId (systematicStudy: UUID): ProtocolDocument
}
