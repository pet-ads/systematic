package br.all.application.review.repository

import br.all.domain.model.review.Collaborator
import br.all.domain.shared.user.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.shared.user.Email
import br.all.domain.shared.user.Role

fun Collaborator.toDto() = CollaboratorDto(
    id.value(),
    systematicStudyId.value(),
    username,
    email.toString(),
    role.toString(),
)

fun Collaborator.Companion.fromDto(dto: CollaboratorDto) = Collaborator(
    ResearcherId(dto.researcherId),
    SystematicStudyId(dto.systematicStudyId),
    dto.username,
    Email(dto.email),
    Role.valueOf(dto.role),
)
