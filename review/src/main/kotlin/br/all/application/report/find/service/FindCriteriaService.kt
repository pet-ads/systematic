package br.all.application.report.find.service

import br.all.application.protocol.repository.CriterionDto
import br.all.application.report.find.presenter.FindCriteriaPresenter
import java.util.*

interface FindCriteriaService {
    fun findCriteria(presenter: FindCriteriaPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val type: String,
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val criteria: FoundStudies
    )

    data class FoundStudies(
        val included: Map<CriterionDto, List<Long>>
    )
}