package br.all.review.requests

import br.all.application.review.find.services.SearchCollaboratorCandidatesService.RequestModel
import java.util.*

data class SearchCandidatesRequest(
    val prefix: String,
) {
    fun toCreateRequestModel(systematicStudyId: UUID) = RequestModel(systematicStudyId, prefix)
}