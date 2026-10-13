package br.all.application.reviewmode.repository

import java.util.UUID

sealed interface ReviewModeStageDto
data class SplitDto(val reviewPerStudy: Int) : ReviewModeStageDto
data class ReplicationDto(val mainReviewer: UUID) : ReviewModeStageDto

data class ReviewModeDto(
    val systematicStudyId: UUID,
    val selection: ReviewModeStageDto? = null,
    val extraction: ReviewModeStageDto? = null
)
