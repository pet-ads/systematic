package br.all.application.report.export.service

import java.util.UUID

data class ConductionExportConfig(
    val includedInFirstSelection: Boolean = false,
    val excludedInFirstSelection: Boolean = false,
    val includedInSecondSelection: Boolean = false,
    val excludedInSecondSelection: Boolean = false,
    val consolidatedExtraction: Boolean = true,
    val funnel: Boolean = true,
)

enum class VisualizationType { PIE_CHART, BAR_CHART, BUBBLE_CHART, LINE_CHART, TABLE, ITEM_TABLE }

data class ExportItemConfig(
    val questionId: UUID,
    val visualization: VisualizationType,
)