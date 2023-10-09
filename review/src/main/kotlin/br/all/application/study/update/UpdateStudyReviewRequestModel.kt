package br.all.application.study.update

import java.util.*

data class UpdateStudyReviewRequestModel (
    val reviewID: UUID,
    val studyReviewId: Long,
    val newStatus: String){
}