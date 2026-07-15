package br.all.infrastructure.report.export

import br.all.application.report.export.ReviewExportData
import br.all.application.report.export.ReviewExporter

class LatexReviewExporter: ReviewExporter{
    override val format = "latex"

    override fun export(data: ReviewExportData): String {
         return LatexDocumentBuilder()
            .addSystematicStudy(data.systematicStudy)
            .addProtocol(data.protocol)
            .addFunnel(data.funnel)
             .addStudies(
                 data.studiesIncludedInScreening,
                 data.studiesExcludedInScreening,
                 data.includedStudies,
                 data.studiesExcludedInFullText,

             )
             .build()
    }
}