package br.all.application.study.update

import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.domain.model.study.StudyReview

class UpdateStudyReviewReadingPriorityService(private val repository: StudyReviewRepository) {

    fun changeStatus(requestModel: UpdateStudyReviewRequestModel){
        val studyReviewDto = repository.findById(requestModel.reviewID, requestModel.studyReviewId)

        val studyReview = StudyReview.fromDto(studyReviewDto)

        when(requestModel.newStatus.uppercase()){
            "VERY_LOW" -> studyReview.markAsVeryLowReadingPriority()
            "LOW" -> studyReview.markAsLowReadingPriority()
            "HIGH" -> studyReview.markAsHighReadingPriority()
            "VERY_HIGH" -> studyReview.markAsVeryHighReadingPriority()
            else -> throw IllegalArgumentException("Unknown study review reading priority: ${requestModel.newStatus}.")
        }

        repository.create(studyReview.toDto())
    }
}