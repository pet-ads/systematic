package br.all.infrastructure.search

import br.all.application.search.repository.SearchSessionDto
import br.all.application.search.repository.SearchSessionRepository
import br.all.domain.model.search.SearchSession
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class SearchSessionRepositoryImpl(val repository: MongoSearchSessionRepository) : SearchSessionRepository {
    override fun create(dto: SearchSessionDto) {
        repository.save(dto.toDocument())
    }

    override fun findById(searchSessionId: UUID): SearchSessionDto? {
        TODO("Not yet implemented")
    }

    override fun getSearchSessionBySource(systematicStudyId: UUID, source: String): SearchSession? {
        TODO("Not yet implemented")
    }

    override fun existsBySearchSource(systematicStudyId: UUID, source: String): Boolean {
        TODO("Not yet implemented")
    }
}