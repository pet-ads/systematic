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
    is SplitDto -> SplitDocument(reviewPerStudy)
    is ReplicationDto -> ReplicationDocument(mainReviewer)
}

private fun ReviewModeStageDocument.toDto(): ReviewModeStageDto = when (this) {
    is SplitDocument -> SplitDto(reviewPerStudy)
    is ReplicationDocument -> ReplicationDto(mainReviewer)
}
