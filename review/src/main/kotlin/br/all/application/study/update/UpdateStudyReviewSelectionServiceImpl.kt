package br.all.application.study.update

import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.update.UpdateStudyReviewSelectionService.RequestModel
import br.all.domain.model.study.StudyReview

class UpdateStudyReviewSelectionServiceImpl(
    private val repository: StudyReviewRepository
) : UpdateStudyReviewSelectionService {

    override fun changeStatus(request: RequestModel){
        val studyReviewDto = repository.findById(request.reviewID, request.studyReviewId)
            ?: throw NoSuchElementException("There is no review with reviewId " +
                    "${request.reviewID} and/or st ID ${request.studyReviewId}")

        val studyReview = StudyReview.fromDto(studyReviewDto)

        when(request.status.uppercase()){
            "UNCLASSIFIED" -> studyReview.unclassifyInSelection()
            "DUPLICATED" -> studyReview.markAsDuplicate()
            "INCLUDED" -> studyReview.includeInSelection()
            "EXCLUDED" -> studyReview.excludeInSelection()
            else -> throw IllegalArgumentException("Unknown study review status: ${request.status}.")
        }
        repository.saveOrUpdate(studyReview.toDto())
    }
}
