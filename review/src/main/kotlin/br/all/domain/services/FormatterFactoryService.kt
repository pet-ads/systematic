package br.all.domain.services

class FormatterFactoryService (
    private val csvFormatterService: CsvFormatterService,
    private val latexFormatterService: LatexFormatterService
) {
    fun format(
        type: String,
        protocol: ProtocolFto,
    ): String {
        return when (type) {
            "csv" -> csvFormatterService.formatProtocol(protocol)
            "latex" -> latexFormatterService.formatProtocol(protocol)
            else -> {"Unsupported format $type"}
        }
    }
}

data class ProtocolFto(
    val id: String,
    val systematicStudy: String,

    val goal: String,
    val justification: String,

    val researchQuestions: List<String>,
    val keywords: List<String>,
    val searchString: String,
    val informationSources: List<String>,
    val sourcesSelectionCriteria: String,

    val searchMethod: String,
    val studiesLanguages: List<String>,
    val studyTypeDefinition: String,

    val selectionProcess: String,
    val eligibilityCriteria: List<String>,

    val dataCollectionProcess: String,
    val analysisAndSynthesisProcess: String,

    val extractionQuestions: List<String>,
    val robQuestions: List<String>,

    val picoc: String
)
