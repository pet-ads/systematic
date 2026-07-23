package br.all.application.collaborator.find

import br.all.application.review.repository.CollaboratorDto
import br.all.application.review.repository.CollaboratorTokenDto
import br.all.application.user.repository.TokenStatus
import br.all.domain.shared.user.Role
import java.util.*

interface FindAllCollaboratorsService {
    fun findAll(presenter: FindAllCollaboratorsPresenter, request: Request)

    data class Request(
        val userId: UUID,
        val systematicStudyId: UUID,
    )

    data class ResponseModel(
        val invited: List<InvitedCollaboratorDto>,
        val collaborators: List<CollaboratorOnReviewDto>,
    )

    data class InvitedCollaboratorDto(
        val id: UUID,
        val username: String,
        val email: String,
        val status: TokenStatus,
    )

    data class CollaboratorOnReviewDto(
        val id: UUID,
        val username: String,
        val email: String,
        val role: Role,
    )

    fun CollaboratorTokenDto.toInvitedCollaboratorDto() =
        InvitedCollaboratorDto(
            id = researcherId,
            username = username,
            email = email,
            status = status
        )

    fun CollaboratorDto.toCollaboratorOnReviewDto() =
        CollaboratorOnReviewDto(
            id = researcherId,
            username = username,
            email = email,
            role = Role.valueOf(role)
        )
}
