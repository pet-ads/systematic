package br.all.infrastructure.collaborator

import br.all.application.collaborator.repository.CollaboratorTokenDto

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
