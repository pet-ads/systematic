package br.all.application.search.find.service

import br.all.application.search.find.presenter.FindSearchSessionPresenter
import br.all.application.search.repository.SearchSessionDto
import java.util.UUID

interface FindSearchSessionService {
    fun findOneSession(presenter: FindSearchSessionPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val sessionId: UUID
    )

    data class ResponseModel(
        val userId: UUID,
        val content: SearchSessionDto
    )
}