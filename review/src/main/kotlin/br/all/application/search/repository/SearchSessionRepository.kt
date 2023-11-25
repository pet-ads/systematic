package br.all.application.search.repository


import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.model.protocol.SearchSource

interface SearchSessionRepository {
    fun create(searchSession: SearchSession)
    fun findById(searchSessionId: SearchSessionID): SearchSession?
    fun getSearchSessionBySource(protocolId: ProtocolId, source: SearchSource): SearchSession?

}
