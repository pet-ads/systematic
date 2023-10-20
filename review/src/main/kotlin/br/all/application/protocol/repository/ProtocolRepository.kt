package br.all.application.protocol.repository

import br.all.domain.model.protocol.Protocol
import br.all.domain.model.protocol.ProtocolId

interface ProtocolRepository {
    fun findById(id: ProtocolId) : Protocol?
}