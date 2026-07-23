package br.all.application.collaborator.find

import br.all.domain.shared.user.Role
import java.util.*

interface FindCollaboratorRoleService {
    fun findRole(presenter: FindCollaboratorRolePresenter, request: Request)

    data class Request(
        val userId: UUID,
        val systematicStudyId: UUID,
    )

    data class ResponseModel(
        val role: Role,
    )
}
