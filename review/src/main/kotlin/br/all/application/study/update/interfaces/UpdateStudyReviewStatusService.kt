package br.all.application.study.update.interfaces

import java.util.*

interface UpdateStudyReviewStatusService {
    fun changeStatus(presenter: UpdateStudyReviewStatusPresenter, request: RequestModel)
    data class RequestModel (val researcherId: UUID, val reviewId: UUID, val studyReviewId: Long, val status: String)
    class ResponseModel

}