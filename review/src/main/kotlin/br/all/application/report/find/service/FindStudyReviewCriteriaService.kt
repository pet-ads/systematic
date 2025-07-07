package br.all.application.report.find.service

import br.all.application.report.find.presenter.FindStudyReviewCriteriaPresenter
import java.util.*

interface FindStudyReviewCriteriaService {
    fun findCriteria(presenter: FindStudyReviewCriteriaPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val inclusionCriteria: Set<String>,
        val exclusionCriteria: Set<String>
    )
}