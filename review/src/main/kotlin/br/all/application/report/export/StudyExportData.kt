package br.all.application.report.export

data class StudyExportData(
    val id: Long,
    val title: String,
    val authors: String,
    val year: Int,
    val venue: String,
    val doi: String?,
    val keywords: Set<String>,
    val selectionCriteria: Set<String>,
    val extractionAnswers: List<QuestionAnswerExportData>,
    val robAnswers: List<QuestionAnswerExportData>
)

data class QuestionAnswerExportData(
    val question: String,
    val answer: String?
)

