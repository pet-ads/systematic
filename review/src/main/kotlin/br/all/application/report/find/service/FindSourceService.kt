package br.all.application.report.find.service

import br.all.application.report.find.presenter.FindSourcePresenter
import java.util.*

interface FindSourceService {
    fun findSource(presenter: FindSourcePresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val source: String,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val source: String,
        val included: List<Long>,
        val excluded: List<Long>,
        val duplicated: List<Long>,
        val totalOfStudies: Int
    )
}