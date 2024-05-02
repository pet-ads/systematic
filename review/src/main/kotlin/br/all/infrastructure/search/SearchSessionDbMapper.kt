package br.all.infrastructure.search

import br.all.application.search.repository.SearchSessionDto

fun SearchSessionDto.toDocument() = SearchSessionDocument(
    id,
    systematicStudyId,
    researcherId,
    searchString,
    additionalInfo,
    timestamp,
    source
)

fun SearchSessionDocument.toDto() = SearchSessionDto(
    id,
    systematicStudyId,
    researcherId,
    searchString,
    additionalInfo,
    timestamp,
    source
)