package br.all.application.study.repository

import br.all.application.study.create.CreateStudyReviewService.RequestModel
import br.all.domain.model.protocol.Criteria
import br.all.domain.model.protocol.Criteria.CriteriaType
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.*
import br.all.domain.shared.utils.Phrase

fun StudyReview.toDto() = StudyReviewDto(
    systematicStudyId.value,
    studyId.value,
    studyType.toString(),
    title,
    year,
    authors,
    venue,
    abstract,
    keywords,
    references,
    doi?.toString(),
    searchSources,
    criteria.associate { it.description.toString() to it.type.name },
    formAnswers.mapValues { (_, answer) -> answer.value.toString() }.toMap(),
    qualityAnswers.mapValues { (_, answer) -> answer.value.toString() }.toMap(),
    comments,
    readingPriority.toString(),
    extractionStatus.toString(),
    selectionStatus.toString()
)

fun StudyReview.Companion.fromDto(dto: StudyReviewDto) = StudyReview(
    StudyReviewId(dto.studyReviewId),
    SystematicStudyId(dto.systematicStudyId),
    StudyType.valueOf(dto.studyType),
    dto.title,
    dto.year,
    dto.authors,
    dto.venue,
    dto.abstract,
    dto.keywords.toMutableSet(),
    dto.searchSources.toMutableSet(),
    dto.references.toMutableList(),
    dto.doi?.let { Doi(it) },
    dto.criteria.map { (description, type) -> Criteria(Phrase(description), CriteriaType.valueOf(type)) }.toMutableSet(),
    dto.formAnswers.mapValues { (questionId, answer) -> Answer(questionId, answer) }.toMutableMap(),
    dto.qualityAnswers.mapValues { (questionId, answer) -> Answer(questionId, answer) }.toMutableMap(),
    dto.comments,
    ReadingPriority.valueOf(dto.readingPriority),
    SelectionStatus.valueOf(dto.selectionStatus),
    ExtractionStatus.valueOf(dto.extractionStatus)
)

fun StudyReview.Companion.fromStudyRequestModel(studyId: Long, request: RequestModel) = StudyReview(
    StudyReviewId(studyId),
    SystematicStudyId(request.systematicStudyId),
    StudyType.valueOf(request.type.uppercase()),
    request.title,
    request.year,
    request.authors,
    request.venue,
    request.abstract,
    request.keywords,
    mutableSetOf(request.source)
)
