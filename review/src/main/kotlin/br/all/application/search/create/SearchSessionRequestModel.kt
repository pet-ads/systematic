package br.all.application.search.create

import br.all.domain.model.protocol.SearchSource
import java.util.UUID

data class SearchSessionRequestModel(
    val systematicStudy: UUID,
    val source: SearchSource,
    val searchString: String,
    val additionalInfo: String?
)