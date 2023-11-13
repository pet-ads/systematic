package br.all.application.study.create

import br.all.application.study.create.CreateStudyReviewService.*
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromStudyRequestModel
import br.all.application.study.repository.toDto
import br.all.domain.model.study.StudyReview
import br.all.domain.services.IdGeneratorService
import org.springframework.stereotype.Service
import java.util.*


@Service
class CreateStudyReviewServiceImpl(
    private val repository: StudyReviewRepository,
    private val presenter: CreateStudyReviewPresenter,
    private val idGenerator: IdGeneratorService
) : CreateStudyReviewService {

    override fun createFromStudy(reviewId: UUID, study: RequestModel) {
        val studyId = idGenerator.next()
        val studyReview = StudyReview.fromStudyRequestModel(reviewId, studyId, study)
        repository.create(studyReview.toDto())
        presenter.prepareSuccessView(ResponseModel(reviewId, studyId))
    }
}


