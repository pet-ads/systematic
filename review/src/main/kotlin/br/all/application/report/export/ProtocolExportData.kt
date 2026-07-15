package br.all.application.report.export

data class ProtocolExportData(
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
    val eligibilityCriteria: List<CriterionExportData>,

    val dataCollectionProcess: String,
    val analysisAndSynthesisProcess: String,

    val extractionQuestions: List<String>,
    val robQuestions: List<String>,

    val picoc: PicocExportData?,
){
    fun inclusionCriteria(): List<String> {
        return eligibilityCriteria
            .filter { it.type == "INCLUSION" }
            .map { it.description }
    }

    fun exclusionCriteria(): List<String> {
        return eligibilityCriteria
            .filter { it.type == "EXCLUSION" }
            .map { it.description }
    }

}

data class CriterionExportData(
    val description: String,
    val type: String
)

data class PicocExportData(
    val population: String? = null,
    val intervention: String? = null,
    val control: String? = null,
    val outcome: String? = null,
    val context: String? = null
)
