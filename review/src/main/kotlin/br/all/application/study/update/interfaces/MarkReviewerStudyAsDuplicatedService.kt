package br.all.application.study.update.interfaces

import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

interface MarkReviewerStudyAsDuplicatedService {

    fun markAsDuplicated(presenter: MarkAsDuplicatedPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val reviewerId: UUID,
        val referenceStudyId: Long,
        val duplicatedStudyIds: List<Long>,
        val stage: StudyReviewStage
    )
}