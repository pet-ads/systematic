package br.all.application.study.update

import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.update.UpdateStudyReviewExtractionService.RequestModel
import br.all.application.study.shared.StudyReviewResponseModel
import br.all.domain.model.study.StudyReview

class UpdateStudyReviewExtractionServiceImpl (
    private val repository: StudyReviewRepository
) : UpdateStudyReviewExtractionService {

    override fun changeStatus(request: RequestModel){
        val studyReviewDto = repository.findById(request.reviewID, request.studyReviewId)
            ?: throw NoSuchElementException("Review of ${request.reviewID} not found.")

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
