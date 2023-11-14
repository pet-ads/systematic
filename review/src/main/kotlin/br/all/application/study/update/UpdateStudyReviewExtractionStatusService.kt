package br.all.application.study.update

import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.domain.model.study.StudyReview
import br.all.domain.shared.utils.requireThatExists

class UpdateStudyReviewExtractionStatusService (private val repository: StudyReviewRepository) {
    fun changeStatus(request: UpdateStudyReviewRequestModel){
        val studyReviewDto = requireThatExists(repository.findById(request.reviewID, request.studyReviewId))
        val studyReview = StudyReview.fromDto(studyReviewDto)

        when(request.status.uppercase()){
            "UNCLASSIFIED" -> studyReview.unclassifyInExtraction()
            "DUPLICATED" -> studyReview.markAsDuplicate()
            "INCLUDED" -> studyReview.includeInExtraction()
            "EXCLUDED" -> studyReview.excludeInExtraction()
            else -> throw IllegalArgumentException("Unknown study review status: ${request.status}.")
        }

        repository.create(studyReview.toDto())
    }
}
