package br.all.application.search.find.service

import br.all.application.search.find.presenter.FindAllSearchSessionsPresenter
import br.all.application.search.repository.SearchSessionDto
import java.util.*

interface FindAllSearchSessionsService {
    fun findAllSearchSessions(presenter: FindAllSearchSessionsPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val searchSessions: List<SearchSessionDto>
    )
}