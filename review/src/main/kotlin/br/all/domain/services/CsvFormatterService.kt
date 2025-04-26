package br.all.domain.services

import br.all.application.protocol.repository.ProtocolDto

class CsvFormatterService() {

    fun formatProtocol(protocol: ProtocolDto): String {
        val headers = listOf(
            "id", "systematicStudy", "goal", "justification",
            "researchQuestions", "keywords", "searchString", "informationSources",
            "sourcesSelectionCriteria", "searchMethod", "studiesLanguages",
            "studyTypeDefinition", "selectionProcess", "eligibilityCriteria",
            "dataCollectionProcess", "analysisAndSynthesisProcess",
            "extractionQuestions", "robQuestions", "picoc"
        )

        val csvRows = mutableListOf<String>()

        csvRows.add(headers.joinToString(","))

        val values = listOf(
            protocol.id.toString(),
            protocol.systematicStudy.toString(),
            protocol.goal.orEmpty(),
            protocol.justification.orEmpty(),
            protocol.researchQuestions.joinToString("; "),
            protocol.keywords.joinToString("; "),
            protocol.searchString.orEmpty(),
            protocol.informationSources.joinToString("; "),
            protocol.sourcesSelectionCriteria.orEmpty(),
            protocol.searchMethod.orEmpty(),
            protocol.studiesLanguages.joinToString("; "),
            protocol.studyTypeDefinition.orEmpty(),
            protocol.selectionProcess.orEmpty(),
            protocol.eligibilityCriteria.joinToString("; ") { it.description },
            protocol.dataCollectionProcess.orEmpty(),
            protocol.analysisAndSynthesisProcess.orEmpty(),
            protocol.extractionQuestions.joinToString("; ") { it.toString() },
            protocol.robQuestions.joinToString("; ") { it.toString() },
            protocol.picoc?.toString() ?: ""
        )

        csvRows.add(values.joinToString(",") { value ->
            "\"${value.replace("\"", "\"\"")}\""
        })

        return csvRows.joinToString("\n")
    }

}
