package br.all.application.reviewmode.repository

import java.util.UUID

sealed class ReviewModeDto(
    open val systematicStudyId: UUID
)

data class SplitDto(
    override val systematicStudyId: UUID
) : ReviewModeDto(systematicStudyId)

data class ReplicationDto(
    override val systematicStudyId: UUID
) : ReviewModeDto(systematicStudyId)
