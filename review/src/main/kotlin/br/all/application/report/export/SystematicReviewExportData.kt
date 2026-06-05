package br.all.application.report.export

import java.util.UUID

data class SystematicReviewExportData(
    val id: UUID,
    val title: String,
    val description: String,
    val owner: String,
    val collaborators: Set<String>,
    val objectives: String
)

