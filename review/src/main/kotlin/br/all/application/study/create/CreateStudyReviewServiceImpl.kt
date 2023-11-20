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
    private val presenter: CreateStudyReviewPresenter,
    private val credentialsService: ResearcherCredentialsService,
    private val idGenerator: IdGeneratorService
) : CreateStudyReviewService {

    override fun createFromStudy(request: RequestModel) {
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolates(presenter, ReviewId(request.reviewId), ResearcherId(request.researcherId))

        val studyId = idGenerator.next()
        val studyReview = StudyReview.fromStudyRequestModel(studyId, request)

        studyReviewRepository.save(studyReview.toDto())
        presenter.prepareSuccessView(ResponseModel(request.researcherId, request.reviewId, studyId))
    }
}


