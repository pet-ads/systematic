package br.all.application.review.find.services

import br.all.application.review.find.presenter.SearchCollaboratorCandidatesPresenter
import br.all.application.user.SearchResearchesService
import br.all.domain.shared.user.Email
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface SearchCollaboratorCandidatesService {
    fun findCandidatesWith(presenter: SearchCollaboratorCandidatesPresenter, request: RequestModel)

    data class RequestModel(
        val systematicStudyId: UUID,
        val prefix: String,
    )

    @Schema(name = "SearchCollaboratorCandidatesServiceResponseModel")
    data class ResponseModel(
        val researchers: List<SearchResearchesService.ResponseModel>,
    )
}
