package br.all.application.study.update

import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.shared.StudyReviewResponseModel
import br.all.domain.model.study.ReadingPriority
import br.all.domain.model.study.StudyReview
import br.all.domain.shared.utils.requireThatExists

class UpdateStudyReviewReadingPriorityService(private val repository: StudyReviewRepository){
    fun changeStatus(request: UpdateStudyReviewRequestModel) : StudyReviewResponseModel {
        val studyReviewDto = requireThatExists(repository.findById(request.reviewID, request.studyReviewId)) {
            "There is not a Study with that reviewId ${request.reviewID} and/or ID ${request.studyReviewId}"
        }
        StudyReview.fromDto(studyReviewDto).apply {
            readingPriority = ReadingPriority.valueOf(request.status)
            repository.create(toDto())
        }

        return StudyReviewResponseModel(request.reviewID, request.studyReviewId)
    }
}