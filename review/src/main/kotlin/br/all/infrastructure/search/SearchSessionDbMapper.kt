package br.all.infrastructure.search

import br.all.application.search.repository.SearchSessionDto

fun SearchSessionDto.toDocument() = SearchSessionDocument(
    id,
    systematicStudyId,
    searchString,
    additionalInfo,
    timestamp,
    source
)