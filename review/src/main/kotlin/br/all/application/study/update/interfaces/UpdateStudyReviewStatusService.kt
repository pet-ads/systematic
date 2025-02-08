package br.all.application.study.update.interfaces

import br.all.domain.model.protocol.Criterion
import java.util.*

interface UpdateStudyReviewStatusService {
    fun changeStatus(presenter: UpdateStudyReviewStatusPresenter, request: RequestModel)

    data class RequestModel (
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
        val status: String,
        val criterion: Criterion? = null
    )

    class ResponseModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long
    )

}