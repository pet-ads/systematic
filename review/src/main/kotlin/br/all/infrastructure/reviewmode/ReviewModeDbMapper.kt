package br.all.infrastructure.reviewmode

import br.all.application.reviewmode.repository.ReplicationDto
import br.all.application.reviewmode.repository.ReviewModeDto
import br.all.application.reviewmode.repository.ReviewModeStageDto
import br.all.application.reviewmode.repository.SplitDto

fun ReviewModeDto.toDocument() = ReviewModeDocument(
    id = systematicStudyId,
    selection = selection?.toDocument(),
    extraction = extraction?.toDocument()
)

fun ReviewModeDocument.toDto() = ReviewModeDto(
    systematicStudyId = id,
    selection = selection?.toDto(),
    extraction = extraction?.toDto()
)

private fun ReviewModeStageDto.toDocument(): ReviewModeStageDocument = when (this) {
    is SplitDto -> SplitDocument(reviewers, reviewPerStudy)
    is ReplicationDto -> ReplicationDocument(mainReviewer, secondaryReviewers, noStudySharedBySecondaries)
}

private fun ReviewModeStageDocument.toDto(): ReviewModeStageDto = when (this) {
    is SplitDocument -> SplitDto(reviewers, reviewPerStudy)
    is ReplicationDocument -> ReplicationDto(mainReviewer, secondaryReviewers, noStudySharedBySecondaries)
}
