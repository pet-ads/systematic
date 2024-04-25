package br.all.application.search.repository
import br.all.application.search.create.CreateSearchSessionService.RequestModel
import br.all.application.search.update.UpdateSearchSessionService
import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import java.time.LocalDateTime

fun SearchSession.toDto() = SearchSessionDto(
    id.value(),
    systematicStudyId.value(),
    researcherId.value(),
    searchString,
    additionalInfo,
    timestamp,
    source.toString()
)

fun SearchSession.Companion.fromRequestModel(id: SearchSessionID, request: RequestModel) = SearchSession(
    id,
    SystematicStudyId(request.systematicStudyId),
    ResearcherId(request.researcherId),
    request.searchString,
    request.additionalInfo ?: "",
    source = SearchSource(request.source),
)

fun SearchSession.Companion.fromDto(dto: SearchSessionDto) = SearchSession(
    SearchSessionID(dto.id),
    SystematicStudyId(dto.systematicStudyId),
    ResearcherId(dto.researcherId),
    dto.searchString,
    dto.additionalInfo,
    dto.timestamp,
    SearchSource(dto.source)
)