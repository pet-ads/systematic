package br.all.application.search.repository

import br.all.application.search.create.SearchSessionRequestModel
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID

fun SearchSession.Companion.fromRequestModel(sessionId: SearchSessionID, systematicStudyId: SystematicStudyId, requestModel: SearchSessionRequestModel): SearchSession {
    return SearchSession(
        sessionId,
        systematicStudyId,
        requestModel.searchString,
        requestModel.additionalInfo ?: "",
        source = requestModel.source
    )
}