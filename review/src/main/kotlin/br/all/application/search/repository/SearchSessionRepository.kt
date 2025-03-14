package br.all.application.search.repository


import br.all.domain.model.search.SearchSession
import java.util.*

interface SearchSessionRepository {
    fun create(dto: SearchSessionDto)
    fun findAllFromSystematicStudy(systematicStudyId: UUID): List<SearchSessionDto>
    fun findById(searchSessionId: UUID): SearchSessionDto?
    fun findSearchSessionsBySource(systematicStudyId: UUID, source: String): List<SearchSessionDto>
    fun existsBySearchSource(systematicStudyId: UUID, source: String): Boolean
    fun saveOrUpdate(dto: SearchSessionDto)
    fun existsById(searchSessionId: UUID): Boolean
    fun deleteById(id: UUID)
}
