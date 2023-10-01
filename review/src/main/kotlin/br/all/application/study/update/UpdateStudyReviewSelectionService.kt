package br.all.application.study.update

import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.domain.model.study.StudyReview

class UpdateStudyReviewSelectionService(private val repository: StudyReviewRepository) {
    //TODO always create request model to wrap the request data
    fun changeStatus(requestModel: UpdateStudyReviewRequestModel){
        //Encontrar StudyReview
        val studyReviewDto = repository.findById(requestModel.reviewID, requestModel.studyReviewId)
        //Trasformar DTO em objeto
        //TODO I solved the map to modifiableMap issue
        val studyReview = StudyReview.fromDto(studyReviewDto)

        //Mudar Selection Status //TODO Remove all comments explaining the code
        when(requestModel.newStatus.uppercase()){
            "UNCLASSIFIED" -> studyReview.unclassifyInSelection()
            "DUPLICATED" -> studyReview.markAsDuplicate()
            "INCLUDED" -> studyReview.includeInSelection()
            "EXCLUDED" -> studyReview.excludeInSelection()
            else -> throw IllegalArgumentException("Unknown study review status: ${requestModel.newStatus}.")
        }

        //Salvar no banco
        repository.create(studyReview.toDto())
    }
}
