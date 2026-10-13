package br.all.application.reviewmode.repository

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.reviewmode.Replication
import br.all.domain.model.reviewmode.ReviewMode
import br.all.domain.model.reviewmode.Split
import br.all.domain.model.reviewmode.SystematicStudyReviewMode
import br.all.domain.shared.user.ResearcherId

fun SystematicStudyReviewMode.toDto() = ReviewModeDto(
    systematicStudyId = systematicStudyId.value(),
    selection = selection?.toDto(),
    extraction = extraction?.toDto()
)

fun ReviewModeDto.toDomain() = SystematicStudyReviewMode(
    systematicStudyId = SystematicStudyId(systematicStudyId),
    selection = selection?.toDomain(),
    extraction = extraction?.toDomain()
)

private fun ReviewMode.toDto(): ReviewModeStageDto = when (this) {
    is Split -> SplitDto(reviewPerStudy)
    is Replication -> ReplicationDto(mainReviewer.value())
}

private fun ReviewModeStageDto.toDomain(): ReviewMode = when (this) {
    is SplitDto -> Split(reviewPerStudy)
    is ReplicationDto -> Replication(ResearcherId(mainReviewer))
}
