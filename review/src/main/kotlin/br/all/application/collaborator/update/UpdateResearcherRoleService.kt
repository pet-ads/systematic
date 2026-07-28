package br.all.application.collaborator.update

import br.all.domain.shared.user.Role
import java.util.*

interface UpdateResearcherRoleService {
    fun update(presenter: UpdateResearcherRolePresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val researcherId: UUID,
        val role: Role,
    )

    data class ResponseModel(
        val researcherId: UUID,
        val role: Role,
    )
}
