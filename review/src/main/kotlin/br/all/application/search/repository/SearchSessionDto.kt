package br.all.application.search.repository


import br.all.domain.model.protocol.ProtocolId
import java.util.*
import br.all.domain.model.protocol.SearchSource
import java.time.LocalDateTime

data class SearchSessionDto(
        val protocolId: UUID,
        val id: UUID,
        val searchString: String,
        val additionalInfo: String = "",
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val source: SearchSource
) {

    //TODO: no need of to string in data classes (they already have toString)

    //don´t know if i´m going to implement toString yet->
    /* override fun toString() = "SearchSessionDto([Timestamp = $timestamp] id=$id, source = '$source', " +
             "searchString='$searchString', additionalInfo='$additionalInfo')"*/
}
