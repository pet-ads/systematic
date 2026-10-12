package br.all.application.reviewmode.repository

import java.util.UUID

sealed interface ReviewModeStageDto
data object SplitDto : ReviewModeStageDto
data object ReplicationDto : ReviewModeStageDto

data class ReviewModeDto(
    val systematicStudyId: UUID,
    val selection: ReviewModeStageDto? = null,
    val extraction: ReviewModeStageDto? = null
)
