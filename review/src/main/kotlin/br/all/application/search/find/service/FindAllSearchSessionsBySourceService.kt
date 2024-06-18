package br.all.application.search.find.service

import br.all.application.search.find.presenter.FindAllSearchSessionsBySourcePresenter
import br.all.application.search.repository.SearchSessionDto
import java.util.*

interface FindAllSearchSessionsBySourceService {
    fun findAllSessionsBySource(presenter: FindAllSearchSessionsBySourcePresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val source: String,
    )
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val source: String,
        val searchSessions: List<SearchSessionDto>
    )
}