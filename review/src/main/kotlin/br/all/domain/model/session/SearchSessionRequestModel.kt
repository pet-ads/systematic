package br.all.domain.model.session

import br.all.domain.model.researcher.ResearcherId
import java.time.LocalDateTime

data class SearchSessionRequestModel(
    val searchString: String,
    val additionalInfo: String?,
    val LocalDateTime: LocalDateTime?,
    val researchers: MutableSet<ResearcherId>
)