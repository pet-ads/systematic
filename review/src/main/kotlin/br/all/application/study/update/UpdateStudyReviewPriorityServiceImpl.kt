package br.all.application.study.update

import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.update.UpdateStudyReviewPriorityService.RequestModel
import br.all.domain.model.study.ReadingPriority
import br.all.domain.model.study.StudyReview

class UpdateStudyReviewPriorityServiceImpl(
    private val repository: StudyReviewRepository
) : UpdateStudyReviewPriorityService {

    override fun changeStatus(request: RequestModel) {
        val studyReviewDto = repository.findById(request.reviewID, request.studyReviewId)
            ?: throw NoSuchElementException("There is no review with reviewId ${request.reviewID} " +
                    "and/or st ID ${request.studyReviewId}")

        val studyReview = StudyReview.fromDto(studyReviewDto).apply {
            readingPriority = ReadingPriority.valueOf(request.status)
        }
        repository.save(studyReview.toDto())
    }
}