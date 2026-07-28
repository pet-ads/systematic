package br.all.infrastructure.collaborator

import br.all.application.collaborator.repository.CollaboratorDto


fun CollaboratorDto.toDocument() = CollaboratorDocument(
    CollaboratorDocument.buildId(researcherId,systematicStudyId),
    researcherId,
    systematicStudyId,
    username,
    email,
    role,
)

fun CollaboratorDocument.toDto() = CollaboratorDto(
    researcherId,
    systematicStudyId,
    username,
    email,
    role
)
