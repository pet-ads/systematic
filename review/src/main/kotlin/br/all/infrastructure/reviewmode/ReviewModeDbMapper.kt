package br.all.infrastructure.reviewmode

import br.all.application.reviewmode.repository.ReplicationDto
import br.all.application.reviewmode.repository.ReviewModeDto
import br.all.application.reviewmode.repository.SplitDto

fun ReviewModeDto.toDocument(): ReviewModeDocument = when (this) {
    is SplitDto -> SplitDocument(id = systematicStudyId)
    is ReplicationDto -> ReplicationDocument(id = systematicStudyId)
}

fun ReviewModeDocument.toDto(): ReviewModeDto = when (this) {
    is SplitDocument -> SplitDto(systematicStudyId = id)
    is ReplicationDocument -> ReplicationDto(systematicStudyId = id)
}
