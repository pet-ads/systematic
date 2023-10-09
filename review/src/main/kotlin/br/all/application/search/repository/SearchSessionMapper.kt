package br.all.application.search.repository


import br.all.application.search.repository.SearchSessionDto
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID

object SearchSessionMapper {
    fun toDto(searchSession: SearchSession): SearchSessionDto {
        return SearchSessionDto(
            id = searchSession.id.value,
            searchString = searchSession.searchString,
            additionalInfo = searchSession.additionalInfo,
            timestamp = searchSession.timestamp,
            source = searchSession.source
        )
    }

    fun fromDto(dto: SearchSessionDto): SearchSession {
        return SearchSession(
            id = SearchSessionID(dto.id),
            searchString = dto.searchString,
            additionalInfo = dto.additionalInfo,
            timestamp = dto.timestamp,
            source = dto.source
        )
    }
}
