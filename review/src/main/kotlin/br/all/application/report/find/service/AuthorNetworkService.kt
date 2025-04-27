package br.all.application.report.find.service

import br.all.application.report.find.presenter.AuthorNetworkPresenter
import java.util.*

interface AuthorNetworkService {
    fun findAuthors(presenter: AuthorNetworkPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val nodes: Set<PaperNode>,
        val edges: List<Edge>
    )
}