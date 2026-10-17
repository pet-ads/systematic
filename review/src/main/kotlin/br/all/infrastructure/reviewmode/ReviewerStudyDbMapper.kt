package br.all.infrastructure.reviewmode

import br.all.application.reviewmode.repository.ReviewerStudyDto
import br.all.infrastructure.study.toDocument
import br.all.infrastructure.study.toDto

fun ReviewerStudyDto.toDocument() = ReviewerStudyDocument(
    id = ReviewerStudyDocumentId(
        systematicStudyId = systematicStudyId,
        researcherId = researcherId,
        studyId = studyId,
    ),
    studyReview = studyReview.toDocument(),
)

fun ReviewerStudyDocument.toDto() = ReviewerStudyDto(
    studyId = id.studyId,
    systematicStudyId = id.systematicStudyId,
    researcherId = id.researcherId,
    studyReview = studyReview.toDto(),
)
