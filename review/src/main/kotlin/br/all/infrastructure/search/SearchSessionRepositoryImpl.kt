package br.all.infrastructure.search


import br.all.application.search.repository.SearchSessionDto
import br.all.application.search.repository.SearchSessionRepository
import br.all.domain.model.search.SearchSession
import br.all.infrastructure.shared.toNullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class SearchSessionRepositoryImpl(val repository: MongoSearchSessionRepository) : SearchSessionRepository {
    override fun create(dto: SearchSessionDto) {
        repository.save(dto.toDocument())
    }

    override fun findById(searchSessionId: UUID): SearchSessionDto? =
        repository.findById(searchSessionId).toNullable()?.toDto()

    override fun existsById(searchSessionId: UUID) = repository.existsById(searchSessionId)

    override fun findAllFromSystematicStudy(systematicStudyId: UUID): List<SearchSessionDto> =
        repository.findAllBySystematicStudyId(systematicStudyId).map { it.toDto() }

    override fun getSearchSessionBySource(systematicStudyId: UUID, source: String): SearchSession? {
        TODO("Not yet implemented")
    }
    override fun existsBySearchSource(systematicStudyId: UUID, source: String) =
        repository.existsBySystematicStudyIdAndSource(systematicStudyId, source)
    override fun saveOrUpdate(dto: SearchSessionDto) {
        repository.save(dto.toDocument())
    }
}