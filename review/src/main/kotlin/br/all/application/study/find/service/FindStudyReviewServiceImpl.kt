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
import br.all.domain.model.review.SystematicStudyId

class FindStudyReviewServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: ResearcherCredentialsService,
) : FindStudyReviewService {

    override fun findOne(presenter: FindStudyReviewPresenter, request: RequestModel) {
        val researcherId = ResearcherId(request.researcherId)
        val systematicStudyId = SystematicStudyId(request.systematicStudyId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        if(presenter.isDone()) return

        val studyReview = studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId)
        if (studyReview === null) {
            val message = "There is no review of id ${request.systematicStudyId} or study of id ${request.studyReviewId}."
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }
        presenter.prepareSuccessView(ResponseModel(request.researcherId, studyReview))
    }
}