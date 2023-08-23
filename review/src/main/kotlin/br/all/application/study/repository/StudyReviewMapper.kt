package br.all.application.study.repository

import br.all.application.study.create.StudyReviewRequestModel
import br.all.domain.model.review.ReviewId
import br.all.domain.model.study.*
import java.util.*

fun StudyReview.toDto() = StudyReviewDto(
    id.value,
    reviewId.value,
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
    formAnswers.mapValues { (_, answer) -> answer.toString() }.toMap(),
    qualityAnswers.mapValues { (_, answer) -> answer.toString() }.toMap(),
    comments,
    readingPriority.toString(),
    extractionStatus.toString(),
    selectionStatus.toString()
)

fun StudyReview.Companion.fromStudyRequestModel(reviewId: UUID, studyId: Long, study: StudyReviewRequestModel) = StudyReview(
    StudyReviewId(studyId),
    ReviewId(reviewId),
    study.title,
    study.year,
    study.authors,
    study.venue,
    study.abstract,
    study.keywords,
    mutableSetOf(study.source)
)
