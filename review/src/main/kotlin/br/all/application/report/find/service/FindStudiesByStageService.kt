package br.all.application.report.find.service

import br.all.application.report.find.presenter.FindStudiesByStagePresenter
import java.util.*

interface FindStudiesByStageService {
    fun findStudiesByStage(presenter: FindStudiesByStagePresenter, request: RequestModel)

    data class StudiesIdAmount(
        val ids: List<Long>,
        val amount: Int,
    )

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val stage: String
    )

    data class ResponseModel(
        val userId: UUID,
        val stage: String,
        val systematicStudyId: UUID,
        val includedStudies: StudiesIdAmount,
        val excludedStudies: StudiesIdAmount,
        val unclassifiedStudies: StudiesIdAmount,
        val duplicatedStudies: StudiesIdAmount
    )
}