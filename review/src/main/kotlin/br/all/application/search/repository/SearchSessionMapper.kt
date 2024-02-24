package br.all.application.search.repository
import br.all.domain.model.search.SearchSession

fun SearchSession.toDto() = SearchSessionDto(
    id.value(),
    systematicStudyId.value(),
    searchString,
    additionalInfo,
    timestamp,
    source.toString()
)

