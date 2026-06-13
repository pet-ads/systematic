package br.all.infrastructure.review

import br.all.application.review.repository.CollaboratorDto


fun CollaboratorDto.toDocument() = CollaboratorDocument(
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
