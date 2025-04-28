package br.all.application.report.find.service

import br.all.application.report.find.presenter.StudiesFunnelPresenter
import java.util.*

interface StudiesFunnelService {
    fun studiesFunnel(presenter: StudiesFunnelPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val totalStudies: Int,
        val totalAfterDuplicates: Int,
        val totalOfExcludedStudies: Int,
        val totalExcludedInExtraction: Int,
        val totalIncluded: Int
    )
}