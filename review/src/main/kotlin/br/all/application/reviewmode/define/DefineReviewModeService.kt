package br.all.application.reviewmode.define

import br.all.domain.model.study.StudyReviewStage
import java.util.UUID

interface DefineReviewModeService {
    fun define(presenter: DefineReviewModePresenter, request: RequestModel)

    enum class ModeType {
        SPLIT,
        REPLICATION
    }

    data class RequestModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val stage: StudyReviewStage,
        val mode: ModeType
    )

    data class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val stage: StudyReviewStage,
        val mode: ModeType
    )
}
