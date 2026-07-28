package br.all.application.collaborator.find

import br.all.application.user.repository.UserSummaryDto
import java.util.*

interface SearchCollaboratorCandidatesService {
    fun findCandidatesWith(presenter: SearchCollaboratorCandidatesPresenter, request: RequestModel)

    data class RequestModel(
        val systematicStudyId: UUID,
        val prefix: String,
    )

    data class ResponseModel(
        val researchers: List<UserSummaryDto>
    )
}
