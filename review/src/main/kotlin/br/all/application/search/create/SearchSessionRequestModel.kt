package br.all.application.search.create

import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.review.ReviewId
import java.time.LocalDateTime
import java.util.UUID

data class SearchSessionRequestModel(
    val reviewId: UUID,
    val source: SearchSource,
    val searchString: String,
    val additionalInfo: String?
)