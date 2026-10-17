package br.all.application.reviewmode.repository

import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.reviewmode.ReviewerStudy
import br.all.domain.model.study.StudyReview
import br.all.domain.shared.user.ResearcherId
import br.all.infrastructure.study.StudyReviewId

fun ReviewerStudy.toDto() = ReviewerStudyDto(
    studyId = studyId.studyReviewId,
    systematicStudyId = systematicStudyId.value(),
    researcherId = researcherId.value,
    studyReview = studyReview.toDto(),
)

fun ReviewerStudyDto.toDomain() = ReviewerStudy(
    studyId = StudyReviewId(systematicStudyId, studyId),
    systematicStudyId = SystematicStudyId(systematicStudyId),
    researcherId = ResearcherId(researcherId),
    studyReview = StudyReview.fromDto(studyReview),
)
