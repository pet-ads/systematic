package br.all.application.study.find.service

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.PreconditionChecker
import br.all.application.study.find.presenter.FindStudyReviewPresenter
import br.all.application.study.find.service.FindStudyReviewService.RequestModel
import br.all.application.study.find.service.FindStudyReviewService.ResponseModel
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.ReviewId

class FindStudyReviewServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val presenter: FindStudyReviewPresenter,
    private val credentialsService: ResearcherCredentialsService,
) : FindStudyReviewService {

    override fun findOne(request: RequestModel) {
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolates(
            presenter,
            ReviewId(request.systematicStudy),
            ResearcherId(request.researcherId)
        )

        val studyReview = studyReviewRepository.findById(request.systematicStudy, request.studyReviewId)
        if (studyReview == null) {
            val message = "There is no review of id ${request.systematicStudy} or study of id ${request.studyReviewId}."
            presenter.prepareFailView(EntityNotFoundException(message))
        }
        presenter.prepareSuccessView(ResponseModel(request.researcherId, studyReview!!))
    }
}