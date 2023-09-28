package br.all.domain.model.session

import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.session.SearchSessionID
import java.time.LocalDateTime

class SearchSessionAGRE(
    val SearchSessionID: SearchSessionID,
    val searchString: String,
    // additionalInfo -> obs.
    val additionalInfo: String?,
    val LocalDateTime: LocalDateTime?,
    val researchers: MutableSet<ResearcherId>
) {
    init {
        require(researchers.isNotEmpty()) { "There must be at least one researcher associated with this search session!" }
    }

    override fun toString(): String {
        return "SearchSession(id=${SearchSessionID}, searchString='$searchString', additionalInfo='$additionalInfo'researchers=$researchers)"
    }
}