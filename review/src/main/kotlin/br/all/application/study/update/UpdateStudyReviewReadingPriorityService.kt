package br.all.application.study.update

import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.domain.model.study.ReadingPriority
import br.all.domain.model.study.StudyReview

class UpdateStudyReviewReadingPriorityService(private val repository: StudyReviewRepository) {
    fun changeStatus(request: UpdateStudyReviewRequestModel){
        val studyReviewDto = repository.findById(request.reviewID, request.studyReviewId)
        StudyReview.fromDto(studyReviewDto).apply {
            readingPriority = ReadingPriority.valueOf(request.status)
            repository.create(toDto())
        }
    }
}