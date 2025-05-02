package br.all.application.report.find.service

import br.all.application.report.find.presenter.FindStudiesByStagePresenter
import java.util.*

interface FindStudiesByStageService {
    fun findStudiesByStage(presenter: FindStudiesByStagePresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val stage: String
    )

    data class ResponseModel(
        val userId: UUID,
        val stage: String,
        val systematicStudyId: UUID,
        val includedStudies: List<Long>,
        val excludedStudies: List<Long>,
        val unclassifiedStudies: List<Long>,
        val duplicatedStudies: List<Long>,
        val totalAmount: Int,
    )
}