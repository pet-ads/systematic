package br.all.application.search.repository


import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.search.SearchSession

interface SearchSessionRepository {
    fun create(dto: SearchSessionDto)
    fun getSearchSessionBySource(protocolId: ProtocolId, source: SearchSource): SearchSession?
}
