package br.all.application.study.update

import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.shared.StudyReviewResponseModel
import br.all.domain.model.study.StudyReview
import br.all.domain.shared.utils.requireThatExists

class UpdateStudyReviewSelectionService(private val repository: StudyReviewRepository) {
    fun changeStatus(request: UpdateStudyReviewRequestModel) : StudyReviewResponseModel{
        val studyReviewDto = requireThatExists(repository.findById(request.reviewID, request.studyReviewId))
        val studyReview = StudyReview.fromDto(studyReviewDto)

        when(request.status.uppercase()){
            "UNCLASSIFIED" -> studyReview.unclassifyInSelection()
            "DUPLICATED" -> studyReview.markAsDuplicate()
            "INCLUDED" -> studyReview.includeInSelection()
            "EXCLUDED" -> studyReview.excludeInSelection()
            else -> throw IllegalArgumentException("Unknown study review status: ${request.status}.")
        }

        repository.create(studyReview.toDto())
        return StudyReviewResponseModel(request.reviewID, request.studyReviewId)
    }
}
