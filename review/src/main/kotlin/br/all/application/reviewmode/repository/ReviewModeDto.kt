package br.all.application.reviewmode.repository

import java.util.UUID

sealed interface ReviewModeStageDto
data class SplitDto(
    val reviewers: List<UUID> = emptyList(),
    val reviewPerStudy: Int,
    val acceptMajority: Boolean = false,
) : ReviewModeStageDto
data class ReplicationDto(
    val mainReviewer: UUID,
    val secondaryReviewers: Map<UUID, Double> = emptyMap(),
    val noStudySharedBySecondaries: Boolean = true,
    val acceptMajority: Boolean = false,
) : ReviewModeStageDto

data class ReviewModeDto(
    val systematicStudyId: UUID,
    val selection: ReviewModeStageDto? = null,
    val extraction: ReviewModeStageDto? = null
)
