package br.all.application.report.export

data class PieBarChartExportData(
    val labels: List<String>,
    val values: List<Int>
)

data class BubbleChartPointExportData(
    val year: Int,
    val group: String,
    val count: Int
)

data class BubbleChartExportData(
    val points: List<BubbleChartPointExportData>
)