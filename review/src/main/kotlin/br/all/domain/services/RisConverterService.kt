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

    private fun convert(ris: String): Study {
        require(ris.isNotBlank()) { "RIS must not be blank." }

        val fieldMap = parseRisFields(ris)
        val primaryTitle = fieldMap["TI"] ?: ""
        val secondaryTitle = fieldMap["T1"] ?: ""
        val year = fieldMap["PY"]?.toIntOrNull() ?: 0
        val authors = fieldMap["AU"] ?: ""
        val type = extractStudyType(ris)
        val abs = fieldMap["AB"] ?: ""
        val keywords = parseKeywords(fieldMap["KW"])
        val references = parseReferences(fieldMap["CR"])
        val doi = fieldMap["doi"]?.let { Doi("https://doi.org/$it") }

        return Study(type, ("$primaryTitle $secondaryTitle").trim(), year, authors, abs, keywords, references, doi)
    }
}