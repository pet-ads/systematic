package br.all.application.study.update

import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.domain.model.study.ExtractionStatus
import br.all.domain.model.study.StudyReview

class UpdateStudyReviewExtractionStatusService (private val repository: StudyReviewRepository) {
    fun changeStatus(requestModel: UpdateStudyReviewRequestModel){
        val studyReviewDto = repository.findById(requestModel.reviewID, requestModel.studyReviewId)

        val studyReview = StudyReview.fromDto(studyReviewDto)

        when(requestModel.newStatus.uppercase()){
            "UNCLASSIFIED" -> studyReview.unclassifyInSelection()
            "DUPLICATED" -> studyReview.markAsDuplicate()
            "INCLUDED" -> studyReview.includeInSelection()
            "EXCLUDED" -> studyReview.excludeInSelection()
            else -> throw IllegalArgumentException("Unknown study review status: ${requestModel.newStatus}.")
        }

        repository.create(studyReview.toDto())
    }
}
