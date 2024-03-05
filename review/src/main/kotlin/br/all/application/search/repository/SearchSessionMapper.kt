package br.all.application.search.repository
import br.all.application.search.create.CreateSearchSessionService.RequestModel
import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID

fun SearchSession.toDto() = SearchSessionDto(
    id.value(),
    systematicStudyId.value(),
    searchString,
    additionalInfo,
    timestamp,
    source.toString()
)

fun SearchSession.Companion.fromRequestModel(id: SearchSessionID, request: RequestModel) = SearchSession(
    id,
    SystematicStudyId(request.systematicStudyId),
    request.searchString,
    request.additionalInfo ?: "",
    source = SearchSource(request.source),
)
