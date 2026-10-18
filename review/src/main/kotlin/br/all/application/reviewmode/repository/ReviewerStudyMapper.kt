package br.all.application.reviewmode.repository

import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.reviewmode.ReviewerStudy
import br.all.domain.model.study.StudyReview
import br.all.domain.shared.user.ResearcherId
import br.all.infrastructure.study.StudyReviewId

fun ReviewerStudy.toDto() = ReviewerStudyDto(
    studyId = studyId,
    systematicStudyId = systematicStudyId.value(),
    researcherId = researcherId.value,
    stage = stage,
    studyReview = studyReview.toDto(),
)

fun ReviewerStudyDto.toDomain() = ReviewerStudy(
    studyId = studyId,
    systematicStudyId = SystematicStudyId(systematicStudyId),
    researcherId = ResearcherId(researcherId),
    stage = stage,
    studyReview = StudyReview.fromDto(studyReview),
)
