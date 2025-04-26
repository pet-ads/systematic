package br.all.domain.services

class CsvFormatterService {

    fun formatProtocol(protocol: ProtocolFto): String {
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
            protocol.id,
            protocol.systematicStudy,
            protocol.goal,
            protocol.justification,
            protocol.researchQuestions.joinToString("; "),
            protocol.keywords.joinToString("; "),
            protocol.searchString,
            protocol.informationSources.joinToString("; "),
            protocol.sourcesSelectionCriteria,
            protocol.searchMethod,
            protocol.studiesLanguages.joinToString("; "),
            protocol.studyTypeDefinition,
            protocol.selectionProcess,
            protocol.eligibilityCriteria.joinToString("; "),
            protocol.dataCollectionProcess,
            protocol.analysisAndSynthesisProcess,
            protocol.extractionQuestions.joinToString("; ") { it },
            protocol.robQuestions.joinToString("; ") { it },
            protocol.picoc
        )

        csvRows.add(values.joinToString(",") { value ->
            "\"${value.replace("\"", "\"\"")}\""
        })

        return csvRows.joinToString("\n")
    }

}
