package br.all.application.study.create

import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromStudyRequestModel
import br.all.application.study.repository.toDto
import br.all.application.study.shared.StudyReviewResponseModel
import br.all.domain.model.study.StudyReview
import br.all.domain.services.IdGeneratorService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreateStudyReviewService(
    private val repository: StudyReviewRepository,
    private val idGenerator: IdGeneratorService
) {
    fun createFromStudy(reviewId: UUID, study: StudyReviewRequestModel): StudyReviewResponseModel {
        val studyId = idGenerator.next()
        val studyReview = StudyReview.fromStudyRequestModel(reviewId, studyId, study)
        repository.create(studyReview.toDto())
        return StudyReviewResponseModel(reviewId, studyId)
    }
}


