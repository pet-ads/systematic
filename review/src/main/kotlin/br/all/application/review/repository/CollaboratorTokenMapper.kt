package br.all.application.review.repository

import br.all.domain.model.review.CollaboratorToken
import br.all.domain.model.review.CollaboratorTokenId

fun CollaboratorToken.toDto() = CollaboratorTokenDto(
    id.value(),
    systematicStudyId,
    researcherId,
    status,
    createdAt,
    expiration,
)

fun CollaboratorToken.Companion.fromDto(dto: CollaboratorTokenDto) = CollaboratorToken(
    CollaboratorTokenId(dto.id),
    dto.systematicStudyId,
    dto.researcherId,
    dto.status,
    dto.createdAt,
    dto.expiration,
)
