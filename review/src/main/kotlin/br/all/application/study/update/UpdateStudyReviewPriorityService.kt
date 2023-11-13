package br.all.application.study.update

import java.util.*

interface UpdateStudyReviewPriorityService {
    fun changeStatus(request: RequestModel)
    data class RequestModel (val reviewID: UUID, val studyReviewId: Long, val status: String)
}