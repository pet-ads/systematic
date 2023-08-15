package br.all.application.study.create

import br.all.domain.model.review.ReviewId
import br.all.domain.model.study.*

fun StudyReview.toDto() = StudyReviewDto(
    id.value,
    reviewId.value,
    title,
    year,
    authors,
    venue,
    abstract,
    keywords.toSet(),
    references.toList(),
    doi.toString(),
    searchSources.toSet(),
    criteria.toSet(),
    formAnswers.mapValues { (_, answer) -> answer.toString() }.toMap(),
    qualityAnswers.mapValues { (_, answer) -> answer.toString() }.toMap(),
    comments,
    readingPriority.toString(),
    extractionStatus.toString(),
    selectionStatus.toString()
)

fun fromDto(dto: StudyReviewDto) = StudyReview(
    StudyReviewId(dto.id),
    ReviewId(dto.reviewId),
    dto.title,
    dto.year,
    dto.authors,
    dto.venue,
    dto.abstract,
    dto.keywords,
    dto.references,
    if(dto.doi != "null") Doi(dto.doi) else null,
    dto.searchSources.toMutableSet(),
    dto.criteria.toMutableSet(),
    dto.formAnswers.mapValues { (key, value) -> Answer(key, value) }.toMutableMap(),
    dto.qualityAnswers.mapValues { (key, value) -> Answer(key, value) }.toMutableMap(),
    dto.comments,
    ReadingPriority.valueOf(dto.readingPriority),
    SelectionStatus.valueOf(dto.selectionStatus),
    ExtractionStatus.valueOf(dto.extractionStatus)
)

fun StudyReview.Companion.fromStudyRequestModel(study: StudyRequestModel) = StudyReview(
    StudyReviewId(study.id),
    ReviewId(study.reviewId),
    study.title,
    study.year,
    study.authors,
    study.venue,
    study.abstract,
    study.keywords,
)
