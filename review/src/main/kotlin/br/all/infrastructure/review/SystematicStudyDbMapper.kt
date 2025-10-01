package br.all.infrastructure.review

import br.all.application.review.repository.SystematicStudyDto

fun SystematicStudyDto.toDocument() = SystematicStudyDocument(
    id,
    title,
    description,
    owner,
    collaborators,
    objectives
)

fun SystematicStudyDocument.toDto() = SystematicStudyDto(
    id,
    title,
    description,
    owner,
    collaborators,
    objectives
)
