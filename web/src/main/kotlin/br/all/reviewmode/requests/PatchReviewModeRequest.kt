package br.all.reviewmode.requests

import br.all.application.reviewmode.define.DefineReviewModeService
import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

data class PatchReviewModeRequest(
    val stage: StudyReviewStage,
    val replication: ReplicationRequest? = null,
    val split: SplitRequest? = null,
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID) =
        DefineReviewModeService.RequestModel(
            userId = userId,
            systematicStudyId = systematicStudyId,
            stage = stage,
            replication = replication?.toRequestModel(),
            split = split?.toRequestModel(),
        )

    data class ReplicationRequest(
        val mainReviewer: UUID,
        val secondaryReviewers: Map<UUID, Double> = emptyMap(),
        val noStudySharedBySecondaries: Boolean = true,
    ) {
        fun toRequestModel() = DefineReviewModeService.ReplicationRequest(mainReviewer, secondaryReviewers, noStudySharedBySecondaries)
    }

    data class SplitRequest(
        val reviewers: List<UUID> = emptyList(),
        val reviewPerStudy: Int,
    ) {
        fun toRequestModel() = DefineReviewModeService.SplitRequest(reviewers, reviewPerStudy)
    }
}
