package br.all.application.study.find.service

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.application.study.find.presenter.FindAllStudyReviewsPresenter
import br.all.application.study.find.service.FindAllStudyReviewsService.RequestModel
import br.all.application.study.find.service.FindAllStudyReviewsService.ResponseModel
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.ReviewId

class FindAllStudyReviewsServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: ResearcherCredentialsService,
) : FindAllStudyReviewsService {

    override fun findAllFromReview(presenter: FindAllStudyReviewsPresenter, request: RequestModel)  {
        val researcherId = ResearcherId(request.researcherId)
        val reviewId = ReviewId(request.reviewId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, reviewId)

        if(presenter.isDone()) return

        val studyReviews = studyReviewRepository.findAllFromReview(request.reviewId)
        presenter.prepareSuccessView(ResponseModel(request.researcherId, request.reviewId, studyReviews))
    }
}