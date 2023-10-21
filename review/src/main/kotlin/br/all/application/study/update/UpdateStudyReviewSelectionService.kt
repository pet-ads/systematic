package br.all.application.study.update

import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.domain.model.study.StudyReview

class UpdateStudyReviewSelectionService(private val repository: StudyReviewRepository) {
    fun changeStatus(request: UpdateStudyReviewRequestModel){
        val studyReviewDto = repository.findById(request.reviewID, request.studyReviewId)
        val studyReview = StudyReview.fromDto(studyReviewDto)

        when(request.status.uppercase()){
            "UNCLASSIFIED" -> studyReview.unclassifyInSelection()
            "DUPLICATED" -> studyReview.markAsDuplicate()
            "INCLUDED" -> studyReview.includeInSelection()
            "EXCLUDED" -> studyReview.excludeInSelection()
            else -> throw IllegalArgumentException("Unknown study review status: ${request.status}.")
        }
        repository.create(studyReview.toDto())
    }
}