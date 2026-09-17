package br.all.infrastructure.report.export

import br.all.application.report.export.ExportItemExportData
import br.all.application.report.export.GroupedTableRow
import br.all.application.report.export.ItemTableExportData
import br.all.application.report.export.ProtocolExportData
import br.all.application.report.export.StudyExportData
import br.all.application.report.export.SystematicReviewExportData
import br.all.application.report.export.TextualTableExportData
import br.all.application.report.export.service.VisualizationType

interface DocumentBuilder<T> {
    fun addSystematicStudy(data: SystematicReviewExportData): DocumentBuilder<T>
    fun addProtocol(data: ProtocolExportData): DocumentBuilder<T>
    fun addFunnelImage(fileName: String): DocumentBuilder<T>
    fun addConsolidatedStudiesTable(studies: List<StudyExportData>): DocumentBuilder<T>
    fun addCriterionTable(title: String, rows: List<GroupedTableRow>): DocumentBuilder<T>
    fun addExportItem(item: ExportItemExportData): DocumentBuilder<T>
    fun build(): T
}