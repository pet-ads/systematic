package br.all.application.search.find.service

import br.all.application.search.find.presenter.FindAllSearchSessionsBySourcePresenter
import br.all.application.search.repository.SearchSessionDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

interface FindAllSearchSessionsBySourceService {
    fun findAllSessionsBySource(presenter: FindAllSearchSessionsBySourcePresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val source: String,
    )
    @Schema(name = "FindAllSearchSessionsBySourceResponseModel")
    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val source: String,
        val searchSessions: List<SearchSessionDto>
    )
}