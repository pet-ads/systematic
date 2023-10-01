package br.all.domain.model.search

import br.all.domain.model.protocol.SearchSource
import br.all.domain.shared.ddd.Entity
import java.time.LocalDateTime


//TODO All search session is an Entity itself
class SearchSession(
    //TODO all variables must start with lowercase (I already fixed)
    id: SearchSessionID,
    val searchString: String,
    val additionalInfo: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val source: SearchSource
) : Entity (id){

    override fun toString() = "SearchSession([Timestamp = $timestamp] id=$id, source = '$source', " +
                "searchString='$searchString', additionalInfo='$additionalInfo')"

}