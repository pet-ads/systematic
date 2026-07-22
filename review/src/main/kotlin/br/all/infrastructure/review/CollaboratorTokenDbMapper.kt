package br.all.infrastructure.review

import br.all.application.review.repository.CollaboratorTokenDto

fun CollaboratorTokenDto.toDocument() = CollaboratorTokenDocument(
    id,
    systematicStudyId,
    researcherId,
    email,
    username,
    status,
    createdAt,
    expiration,
)

fun CollaboratorTokenDocument.toDto() = CollaboratorTokenDto(
    id,
    systematicStudyId,
    researcherId,
    email,
    username,
    status,
    createdAt,
    expiration,
)
