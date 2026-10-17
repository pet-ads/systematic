package br.all.infrastructure.report.export

import br.all.application.report.export.ReviewExportData
import br.all.application.report.export.ReviewExporter

class LatexReviewExporter : ReviewExporter {
    override val format = "latex"

    override fun export(data: ReviewExportData): String {
        val builder = LatexDocumentBuilder()
            .addSystematicStudy(data.systematicStudy)
            .addProtocol(data.protocol)

        with(data.conduction) {
            funnelImageFileName?.let { builder.addFunnelImage(it) }
            consolidatedExtraction?.let { builder.addConsolidatedStudiesTable(it) }
            includedInFirstSelection?.let { builder.addCriterionTable("Estudos Incluídos na Primeira Seleção", it) }
            excludedInFirstSelection?.let { builder.addCriterionTable("Estudos Excluídos na Primeira Seleção", it) }
            includedInSecondSelection?.let { builder.addCriterionTable("Estudos Incluídos na Segunda Seleção", it) }
            excludedInSecondSelection?.let { builder.addCriterionTable("Estudos Excluídos na Segunda Seleção", it) }
        }

        data.exportItems.forEach { item -> builder.addExportItem(item) }

        return builder.build()
    }
}