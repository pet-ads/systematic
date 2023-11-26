package br.all.application.study.create

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.application.study.create.CreateStudyReviewService.RequestModel
import br.all.application.study.create.CreateStudyReviewService.ResponseModel
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromStudyRequestModel
import br.all.application.study.repository.toDto
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.ReviewId
import br.all.domain.model.study.StudyReview
import br.all.domain.services.IdGeneratorService

class CreateStudyReviewServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: ResearcherCredentialsService,
    private val idGenerator: IdGeneratorService
) : CreateStudyReviewService {

    override fun createFromStudy(presenter: CreateStudyReviewPresenter, request: RequestModel) {
        val researcherId = ResearcherId(request.researcherId)
        val reviewId = ReviewId(request.reviewId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, reviewId)

        if(presenter.isDone()) return

        val studyId = idGenerator.next()
        val studyReview = StudyReview.fromStudyRequestModel(studyId, request)

        studyReviewRepository.saveOrUpdate(studyReview.toDto())
        presenter.prepareSuccessView(ResponseModel(request.researcherId, request.reviewId, studyId))
    }
}


