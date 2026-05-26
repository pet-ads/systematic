package br.all.application.report.export

interface ReviewExporter {
    val format: String
    fun export(data: ReviewExportData): String

}