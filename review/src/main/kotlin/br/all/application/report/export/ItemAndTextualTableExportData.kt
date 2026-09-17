package br.all.application.report.export

data class ItemTableExportData(
    val options: List<String>,
    val rows: List<ItemTableRow>
)

data class ItemTableRow(
    val studyId: Long,
    val selections: Map<String, Boolean>
)

data class TextualTableExportData(
    val rows: List<TextualTableRow>
)

data class TextualTableRow(
    val studyId: Long,
    val title: String,
    val authors: String,
    val answer: String
)