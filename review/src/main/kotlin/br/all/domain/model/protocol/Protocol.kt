package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Entity

class Protocol (id: ProtocolId) : Entity(id) {
    private val _sources = listOf(SearchSource("ACM"), SearchSource("Scopus"))

    fun hasSource(source: SearchSource) = _sources.contains(source)
}