package br.all.infrastructure.review

import br.all.application.review.repository.CollaboratorTokenDto
import br.all.application.review.repository.SystematicStudyDto

fun CollaboratorTokenDto.toDocument() = CollaboratorTokenDocument(
    id,
    systematicStudyId,
    researcherId,
    status,
    createdAt,
    expiration,
)

fun CollaboratorTokenDocument.toDto() = CollaboratorTokenDto(
    id,
    systematicStudyId,
    researcherId,
    status,
    createdAt,
    expiration,
)
