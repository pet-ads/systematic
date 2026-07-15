package br.all.application.study.update.interfaces

import br.all.domain.model.study.StudyReviewStage
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

interface MarkAsDuplicatedService {

    fun markAsDuplicated(presenter: MarkAsDuplicatedPresenter, request: RequestModel)

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val referenceStudyId: Long,
        val duplicatedStudyIds: List<Long>,
        val stage: StudyReviewStage
    )

    @Schema(name = "MarkAsDuplicatedServiceResponseModel")
    class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val referenceStudyId: Long,
        val duplicatedStudies: List<Long>
    )
}
