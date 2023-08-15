package br.all.modules.application.study.create

import br.all.modules.domain.model.study.StudyReview
import org.springframework.stereotype.Service

@Service
class CreateStudyReviewService(private val repository: StudyReviewRepository) {

    fun createFromStudy(study: StudyRequestModel) {
        val studyReview = StudyReview.fromStudyRequestModel(study)
        repository.create(studyReview.toDto())
    }
}


