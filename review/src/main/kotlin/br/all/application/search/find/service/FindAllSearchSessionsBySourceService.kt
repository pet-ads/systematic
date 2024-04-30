package br.all.application.search.find.service

import br.all.application.search.find.presenter.FindAllSearchSessionsBySourcePresenter
import br.all.application.search.repository.SearchSessionDto
import java.util.*

interface FindAllSearchSessionBySourceService {
    fun findAllBySource(presenter: FindAllSearchSessionsBySourcePresenter, request: RequestModel)

    data class RequestModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val searchSource: String,
    )
    data class ResponseModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val searchSource: String,
        val searchSessions: List<SearchSessionDto>
    )
}