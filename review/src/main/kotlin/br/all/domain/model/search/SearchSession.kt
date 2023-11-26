package br.all.domain.model.search

import br.all.application.search.create.SearchSessionRequestModel
import br.all.application.search.find.SearchSessionResponseModel
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.protocol.SearchSource
import br.all.domain.shared.ddd.Entity
import java.time.LocalDateTime
import java.util.*

class SearchSession(
    val searchSessionId: SearchSessionID,
    val protocolId: ProtocolId,
    val searchString: String,
    val additionalInfo: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val source: SearchSource
) : Entity(searchSessionId){
    fun toDto(): SearchSessionResponseModel {
        return SearchSessionResponseModel(
            id.toString(),
            "Search session created successfully."
        )
    }

    companion object {
        fun fromRequestModel(sessionId: SearchSessionID, protocolId: ProtocolId, requestModel: SearchSessionRequestModel): SearchSession {
            return SearchSession(
                sessionId,
                protocolId,
                requestModel.searchString,
                requestModel.additionalInfo ?: "",
                source = requestModel.source
            )
        }


    }

}