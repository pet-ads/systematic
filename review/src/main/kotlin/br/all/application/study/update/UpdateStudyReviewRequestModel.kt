package br.all.application.study.update

import java.util.*

//TODO ApplicationService must not expose domain classes such as StudyReviewSeletionStatus
data class UpdateStudyReviewRequestModel (
    val reviewID: UUID,
    val studyReviewId: Long,
    val newStatus: String){
}