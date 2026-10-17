package br.all.application.reviewmode.repository

import br.all.application.study.repository.StudyReviewDto
import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

data class ReviewerStudyDto(
    val studyId: Long,
    val systematicStudyId: UUID,
    val researcherId: UUID,
    val stage: StudyReviewStage,
    val studyReview: StudyReviewDto,
)
