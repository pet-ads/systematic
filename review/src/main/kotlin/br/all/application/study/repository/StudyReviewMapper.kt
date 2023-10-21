package br.all.application.study.repository

import br.all.application.study.create.StudyReviewRequestModel
import br.all.domain.model.review.ReviewId
import br.all.domain.model.study.*
import java.util.*

fun StudyReview.toDto() = StudyReviewDto(
    studyId.value,
    reviewId.value,
    studyType.toString(),
    title,
    year,
    authors,
    venue,
    abstract,
    keywords,
    references,
    doi.toString(),
    searchSources,
    criteria,
    formAnswers.mapValues { (_, answer) -> answer.value.toString() }.toMap(),
    qualityAnswers.mapValues { (_, answer) -> answer.value.toString() }.toMap(),
    comments,
    readingPriority.toString(),
    extractionStatus.toString(),
    selectionStatus.toString()
)

fun StudyReview.Companion.fromDto(dto: StudyReviewDto ) = StudyReview(
    StudyReviewId(dto.id),
    ReviewId(dto.reviewId),
    StudyType.valueOf(dto.studyType),
    dto.title,
    dto.year,
    dto.authors,
    dto.venue,
    dto.abstract,
    dto.keywords.toMutableSet(),
    dto.searchSources.toMutableSet(),
    dto.references.toMutableList(),
    Doi(dto.doi),
    dto.criteria.toMutableSet(),
    dto.formAnswers.mapValues { (questionId, answer) -> Answer(questionId, answer) }.toMutableMap(),
    dto.qualityAnswers.mapValues { (questionId, answer) -> Answer(questionId, answer) }.toMutableMap(),
    dto.comments,
    ReadingPriority.convertStringToReadingPriorityEnum(dto.readingPriority),
    SelectionStatus.convertStringToSelectionStatusEnum(dto.selectionStatus),
    ExtractionStatus.convertStringToExtractionStatusEnum(dto.extractionStatus)
)

fun StudyReview.Companion.fromStudyRequestModel(reviewId: UUID, studyId: Long, study: StudyReviewRequestModel) = StudyReview(
    StudyReviewId(studyId),
    ReviewId(reviewId),
    StudyType.valueOf(study.type.uppercase()),
    study.title,
    study.year,
    study.authors,
    study.venue,
    study.abstract,
    study.keywords,
    mutableSetOf(study.source)
)
