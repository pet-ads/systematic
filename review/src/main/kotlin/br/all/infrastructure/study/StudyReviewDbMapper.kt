package br.all.infrastructure.study

import br.all.application.study.repository.StudyReviewDto

fun StudyReviewDocument.toDto() = StudyReviewDto(
    id.studyReviewId,
    id.systematicStudyId,
    searchSessionId,
    type,
    title,
    year,
    authors,
    venue,
    abstractText,
    keywords,
    references,
    doi,
    searchSources,
    criteria,
    formAnswers,
    qualityAnswers,
    comments,
    readingPriority,
    extractionStatus,
    selectionStatus,
    score
)

fun StudyReviewDto.toDocument() = StudyReviewDocument(
    StudyReviewId(systematicStudyId, studyReviewId),
    searchSessionId,
    studyType,
    title,
    year,
    authors,
    venue,
    abstract,
    keywords,
    references,
    doi,
    searchSources,
    criteria,
    formAnswers,
    robAnswers,
    comments,
    readingPriority,
    extractionStatus,
    selectionStatus,
    score
)