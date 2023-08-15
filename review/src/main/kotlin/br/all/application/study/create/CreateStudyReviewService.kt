package br.all.application.study.create

import br.all.domain.model.study.StudyReview
import org.springframework.stereotype.Service

@Service
class CreateStudyReviewService(private val repository: StudyReviewRepository) {

    fun ofStudy(study: StudyRequestModel) {
        val studyReview = StudyReview.fromStudyRequestModel(study)
        repository.create(studyReview.toDto())
    }
}


