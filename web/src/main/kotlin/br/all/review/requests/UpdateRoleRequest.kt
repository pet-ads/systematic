package br.all.review.requests

import br.all.application.review.update.services.UpdateResearcherRoleService
import br.all.domain.shared.user.Role
import java.util.UUID

data class UpdateRoleRequest(val researcherId: UUID, val role: Role) {
    fun toUpdateRequestModel(userId: UUID, systematicStudyId: UUID, researcherId: UUID, role: Role) =
        UpdateResearcherRoleService.RequestModel(userId, systematicStudyId, researcherId, role)
}