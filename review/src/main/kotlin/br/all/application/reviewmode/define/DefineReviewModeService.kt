package br.all.application.reviewmode.define

import br.all.domain.model.reviewmode.Replication
import br.all.domain.model.reviewmode.ReviewMode
import br.all.domain.model.reviewmode.Split
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.user.ResearcherId
import java.util.UUID


interface DefineReviewModeService {
    fun define(presenter: DefineReviewModePresenter, request: RequestModel)

    data class ReplicationRequest(
        val mainReviewer: UUID,
        val secondaryReviewers: Map<UUID, Double> = emptyMap(),
        val noStudySharedBySecondaries: Boolean = true,
    ) {
        fun toReviewMode(): ReviewMode = Replication(
            ResearcherId(mainReviewer),
            secondaryReviewers.mapKeys { ResearcherId(it.key) },
            noStudySharedBySecondaries,
        )
    }

    data class SplitRequest(
        val reviewers: List<UUID> = emptyList(),
        val reviewPerStudy: Int,
    ) {
        fun toReviewMode(): ReviewMode = Split(reviewers.map(::ResearcherId), reviewPerStudy)
    }

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val stage: StudyReviewStage,
        val replication: ReplicationRequest? = null,
        val split: SplitRequest? = null,
    ) {
        init {
            require((replication == null) != (split == null)) {
                "Provide either replication or split data"
            }
        }

        fun toReviewMode(): ReviewMode = when {
            replication != null -> replication.toReviewMode()
            else -> requireNotNull(split).toReviewMode()
        }
    }

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val stage: StudyReviewStage,
    )
}
