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
        val totalIdentifiedBySource: Map<String, Int>,                             // Total de estudos encontrados fazer por source
        val totalAfterDuplicatesRemovedBySource: Map<String, Int>,                 // Após remover duplicados fazer por source
        val totalScreened: Int,                                                    // Total analisado na selection
        val totalExcludedInScreening: Int,                                         // Excluídos na selection
        val excludedByCriterion: Map<String, Int>,
        val totalFullTextAssessed: Int,                                            // Avaliados por texto completo
        val totalExcludedInFullText: Int,                                          // Excluídos após leitura completa (extraction)
        val totalExcludedByCriterion: Map<String, Int>,                            // Excluídos por critério
        val totalIncluded: Int
    )
}