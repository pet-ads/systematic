package br.all.application.report.find.service

import br.all.application.report.find.presenter.ExportProtocolPresenter
import java.util.*

interface ExportProtocolService {
    fun exportProtocol(presenter: ExportProtocolPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val formattedProtocol: String,
    )
}