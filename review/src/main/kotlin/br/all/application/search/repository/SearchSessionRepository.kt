package br.all.application.search.repository


import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.model.protocol.SearchSource
import java.util.*

interface SearchSessionRepository {
    fun create(dto: SearchSessionDto)
    fun findById(searchSessionId: UUID): SearchSessionDto?
    fun getSearchSessionBySource(systematicStudyId: UUID, source: String): SearchSession?
    fun existsBySearchSource(systematicStudyId: UUID, source: String): Boolean
}
