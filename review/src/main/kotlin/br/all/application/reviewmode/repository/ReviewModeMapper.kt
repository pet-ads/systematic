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

fun ReviewMode.toDto(): ReviewModeStageDto = when (this) {
    is Split -> SplitDto(reviewers.map { it.value() }, reviewPerStudy, acceptMajority)
    is Replication -> ReplicationDto(
        mainReviewer = mainReviewer.value(),
        secondaryReviewers = secondaryReviewers.mapKeys { it.key.value() },
        noStudySharedBySecondaries = noStudySharedBySecondaries,
        acceptMajority = acceptMajority,
    )
}

fun ReviewModeStageDto.toDomain(): ReviewMode = when (this) {
    is SplitDto -> Split(reviewers.map(::ResearcherId), reviewPerStudy, acceptMajority)
    is ReplicationDto -> Replication(
        mainReviewer = ResearcherId(mainReviewer),
        secondaryReviewers = secondaryReviewers.mapKeys { ResearcherId(it.key) },
        noStudySharedBySecondaries = noStudySharedBySecondaries,
        acceptMajority = acceptMajority,
    )
}
