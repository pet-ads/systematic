package br.all.infrastructure.search

import br.all.application.search.repository.SearchSessionDto
import br.all.infrastructure.study.StudyReviewDocument
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface MongoSearchSessionRepository : MongoRepository<SearchSessionDocument, UUID>{
    fun existsBySystematicStudyIdAndSource(systematicStudyId: UUID, source: String): Boolean
    fun findAllBySystematicStudyId(systematicStudyId: UUID): List<SearchSessionDocument>
    fun findAllBySource(systematicStudyId: UUID, source: String): List<SearchSessionDocument>
}