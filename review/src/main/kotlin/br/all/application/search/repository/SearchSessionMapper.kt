package br.all.application.search.repository


import br.all.application.search.repository.SearchSessionDto
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID

object SearchSessionMapper {

    //TODO it is better to use Kotlin extension functions
    fun SearchSession.toDto() = SearchSessionDto(
            protocolId.value,
            searchSessionId.value,
            searchString,
            additionalInfo,
            timestamp,
            source
    )


    fun SearchSession.fromDto(dto: SearchSessionDto) = SearchSession(
            SearchSessionID(dto.id),
            ProtocolId(dto.protocolId),
            dto.searchString,
            dto.additionalInfo,
            dto.timestamp,
            dto.source
        )

}
