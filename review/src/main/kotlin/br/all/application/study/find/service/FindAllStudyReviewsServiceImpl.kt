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
    private val presenter: FindAllStudyReviewsPresenter,
    private val credentialsService: ResearcherCredentialsService,
) : FindAllStudyReviewsService {

    override fun findAllFromReview(request: RequestModel)  {
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolates(presenter, ReviewId(request.reviewId), ResearcherId(request.researcherId))

        val studyReviews = studyReviewRepository.findAllFromReview(request.reviewId)
        presenter.prepareSuccessView(ResponseModel(request.researcherId, request.reviewId, studyReviews))
    }
}