package br.all.application.report.export

import br.all.application.report.export.service.VisualizationType
import java.util.UUID

data class ReviewExportData(
    val systematicStudy: SystematicReviewExportData,
    val protocol: ProtocolExportData,
    val conduction: ConductionExportSections,
    val exportItems: List<ExportItemExportData>
)

data class ConductionExportSections(
    val includedInFirstSelection: List<GroupedTableRow>? = null,
    val excludedInFirstSelection: List<GroupedTableRow>? = null,
    val includedInSecondSelection: List<GroupedTableRow>? = null,
    val excludedInSecondSelection: List<GroupedTableRow>? = null,
    val consolidatedExtraction: List<StudyExportData>? = null,
    val funnelImageFileName: String? = null
)

data class ExportItemExportData(
    val questionId: UUID,
    val questionDescription: String,
    val visualization: VisualizationType,
    val tableRows: List<GroupedTableRow>? = null,
    val itemTable: ItemTableExportData? = null,
    val textualTable: TextualTableExportData? = null,
    val pieBarData: PieBarChartExportData? = null,
    val bubbleData: BubbleChartExportData? = null
)