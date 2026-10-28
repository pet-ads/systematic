package br.all.application.reviewmode.find

import br.all.domain.model.reviewmode.Replication
import br.all.domain.model.reviewmode.ReviewMode
import br.all.domain.model.reviewmode.Split
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface FindReviewModeService {
    fun find(presenter: FindReviewModePresenter, request: RequestModel)

    data class RequestModel(val userId: UUID, val systematicStudyId: UUID)

    @Schema(name = "FindReviewModeResponse")
    data class ResponseModel(
        val systematicStudyId: UUID,
        val selection: ReviewModeStageResponse?,
        val extraction: ReviewModeStageResponse?,
    )

    data class ReviewModeStageResponse(
        val split: SplitResponse? = null,
        val replication: ReplicationResponse? = null,
    )

    data class SplitResponse(
        val reviewers: List<UUID>,
        val reviewPerStudy: Int,
        val acceptMajority: Boolean,
    )

    data class ReplicationResponse(
        val mainReviewer: UUID,
        val secondaryReviewers: Map<UUID, Double>,
        val noStudySharedBySecondaries: Boolean,
        val acceptMajority: Boolean,
    )

    fun ReviewMode.toStageResponse(): ReviewModeStageResponse = when (this) {
        is Split -> ReviewModeStageResponse(split = SplitResponse(
            reviewers = reviewers.map { it.value },
            reviewPerStudy = reviewPerStudy,
            acceptMajority = acceptMajority,
        ))

        is Replication ->  ReviewModeStageResponse(
            replication =  ReplicationResponse(
                mainReviewer = mainReviewer.value,
                secondaryReviewers = secondaryReviewers.mapKeys { it.key.value },
                noStudySharedBySecondaries = noStudySharedBySecondaries,
                acceptMajority = acceptMajority,
            )
        )
    }
}
