package br.all.application.report.export

data class GroupedTableRow(
    val criterion: String,
    val studyIds: List<Long>,
    val count: Int,
    val percentage: Double
)