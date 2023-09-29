package br.all.application.study.change

import br.all.application.study.create.StudyReviewRequestModel
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromStudyRequestModel
import br.all.domain.model.review.ReviewId
import br.all.domain.model.study.SelectionStatus
import br.all.domain.model.study.StudyReview
import br.all.domain.model.study.StudyReviewId
import java.util.UUID

class ChangeStudyReview(
    private val repository: StudyReviewRepository
) {
    fun changeSelectionStatus(reviewID: ReviewId, studyReviewId: StudyReviewId, newSelectionStatus: SelectionStatus){
        //Encontrar StudyReview
        val studyReviewDTO = repository.findById(reviewID.value, studyReviewId.value)

    }
}
