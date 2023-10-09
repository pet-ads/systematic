package br.all.domain.model.search

import br.all.domain.model.protocol.SearchSource
import java.time.LocalDateTime

class SearchSession(
    val id: SearchSessionID,
    val searchString: String,
    val additionalInfo: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val source: SearchSource
) {
    override fun toString() = "SearchSession([Timestamp = $timestamp] id=$id, source = '$source', " +
            "searchString='$searchString', additionalInfo='$additionalInfo')"
}
