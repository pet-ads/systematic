package br.all.domain.model.search

import br.all.application.search.create.SearchSessionRequestModel
import br.all.application.search.find.SearchSessionResponseModel
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.protocol.SearchSource
import br.all.domain.shared.ddd.Entity
import java.time.LocalDateTime
import java.util.UUID


class SearchSession(
    searchSessionId: SearchSessionID,
    val protocolId: ProtocolId,
    val searchString: String,
    val additionalInfo: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val source: SearchSource
) : Entity<UUID>(searchSessionId){

    companion object
}