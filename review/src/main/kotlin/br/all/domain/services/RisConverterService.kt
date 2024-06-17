package br.all.domain.services

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.*
import java.util.*

class RisConverterService(private val studyReviewIdGeneratorService: IdGeneratorService) {
    fun convertToStudyReview(systematicStudyId: SystematicStudyId, ris: String): StudyReview {
        require(ris.isNotBlank()) { "RIS must not be blank." }

        val studyReviewId = StudyReviewId(studyReviewIdGeneratorService.next())
        val study = convert(ris)

        return StudyReview(
            studyReviewId,
            systematicStudyId,
            study.type,
            study.title,
            study.year,
            study.authors,
            study.venue,
            study.abstract,
            study.doi,
            study.keywords,
            mutableSetOf("insert SearchSources"),
            study.references,
            mutableSetOf(),
            mutableSetOf(),
            mutableSetOf(),
            "",
            ReadingPriority.LOW,
            SelectionStatus.UNCLASSIFIED,
            ExtractionStatus.UNCLASSIFIED
        )
    }

    fun convertManyToStudyReview(systematicStudyId: SystematicStudyId, ris: String): List<StudyReview> {
        require(ris.isNotBlank()) { "RIS must not be blank." }
        val studies = convertMany(ris)
        return studies.map { convertToStudyReview(systematicStudyId, ris) }
    }

    fun convertMany(ris: String): List<Study> {
        require(ris.isNotBlank()) { "RIS must not be blank." }
        return ris.splitToSequence("TY")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { convert(it) }
            .toList()
    }

    private fun convert(ris: String): Study {
        require(ris.isNotBlank()) { "RIS must not be blank." }

        val fieldMap = parseRisFields(ris) // coloca o RIS em um FieldMap
        val venue = fieldMap["JO"] ?: ""
        val primaryTitle = fieldMap["TI"] ?: ""
        val secondaryTitle = fieldMap["T2"] ?: ""
        val year = fieldMap["PY"]?.toIntOrNull() ?: 0
        val authors = parseAuthors(fieldMap)
        val type = extractStudyType(ris)
        val abs = fieldMap["AB"] ?: ""
        val keywords = parseKeywords(fieldMap["KW"])
        val references = parseReferences(fieldMap["CR"])
        val doi = fieldMap["DO"]?.let { Doi("https://doi.org/$it") }

        return Study(type, ("$primaryTitle $secondaryTitle").trim(), year, authors, venue, treatAbstract(abs), keywords, references, doi)
    }

    fun parseRisFields(ris: String): Map<String, String> {
        val fieldMap = mutableMapOf<String, String>()
        val lines = ris.trim().lines()
        var currentKey: String? = null

        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.contains(" - ")) {
                val keyValuePair = trimmedLine.split(" - ", limit = 2)
                if (keyValuePair.size == 2) {
                    currentKey = keyValuePair[0].trim()
                    val value = keyValuePair[1].trim()
                    if (currentKey == "AU") {
                        fieldMap[currentKey] = fieldMap.getOrDefault(currentKey, "") + value + "; "
                    } else if (currentKey == "KW"){
                        fieldMap[currentKey] = fieldMap.getOrDefault(currentKey, "") + value + "; "
                    } else {
                        fieldMap[currentKey] = value
                    }
                }
            } else if (currentKey != null) {
                fieldMap[currentKey] = "${fieldMap[currentKey]} $trimmedLine".trim()
            }
        }
        return fieldMap
    }

    fun extractStudyType(risEntry: String): StudyType {
        val entryTypeRegex = Regex("""(?m)^TY\s*-\s*(.+)$""")
        val matchResult = entryTypeRegex.find(risEntry)
        val studyTypeName = translateToStudyType(matchResult?.groupValues?.get(1) ?: "")
        return studyTypeName
    }


}