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
        val totalIdentifiedBySource: Map<String, Int>,                             // Total de estudos encontrados, fazer por source
        val totalAfterDuplicatesRemovedBySource: Map<String, Int>,                 // Remover os duplicados do total, e depois, fazer por source
        val totalScreened: Int,                                                    // Total analisado na selection
        val totalExcludedInScreening: Int,                                         // Total excluídos na selection
        val excludedByCriterion: Map<String, Int>,                                 // Dos excluídos na selection, fazer por criteria
        val totalFullTextAssessed: Int,                                            // Total analisados na extraction (passou da selection)
        val totalExcludedInFullText: Int,                                          // Total excluídos na extraction
        val totalExcludedByCriterion: Map<String, Int>,                            // Dos excluídos na extraction, fazer por criteria
        val totalIncluded: Int                                                     // Total incluído (passou na selection e extraction)
    )
}