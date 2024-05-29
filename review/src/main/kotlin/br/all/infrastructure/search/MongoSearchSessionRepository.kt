package br.all.infrastructure.search

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface MongoSearchSessionRepository : MongoRepository<SearchSessionDocument, UUID>{
    fun existsBySystematicStudyIdAndSource(systematicStudyId: UUID, source: String): Boolean
    fun findAllBySystematicStudyId(systematicStudyId: UUID): List<SearchSessionDocument>
    fun findAllBySystematicStudyIdAndSource(systematicStudyId: UUID, source: String): List<SearchSessionDocument>
}