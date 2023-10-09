package br.all.application.search.repository


import java.util.*
import br.all.domain.model.protocol.SearchSource
import java.time.LocalDateTime

data class SearchSessionDto(
    val id: UUID,
    val searchString: String,
    val additionalInfo: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val source: SearchSource
) {

    //don´t know if i´m going to implement toString yet->
   /* override fun toString() = "SearchSessionDto([Timestamp = $timestamp] id=$id, source = '$source', " +
            "searchString='$searchString', additionalInfo='$additionalInfo')"*/
}
