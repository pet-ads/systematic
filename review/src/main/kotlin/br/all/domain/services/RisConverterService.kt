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

}