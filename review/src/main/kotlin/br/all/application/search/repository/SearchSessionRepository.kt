package br.all.application.search.repository


import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID

interface SearchSessionRepository {
    fun getById(searchSessionId: SearchSessionID): SearchSession?
    fun getBySource(source: SearchSource): SearchSession?
    fun create(searchSession: SearchSession)
    abstract fun getSessionBySource(source: SearchSource): Any

    //update and delete will be implemented at other moment
}
