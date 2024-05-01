package br.all.application.study.update.implementation

import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.PreconditionChecker
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromStudyUpdateRequestModel
import br.all.application.study.repository.toDto
import br.all.application.study.update.interfaces.UpdateStudyReviewPresenter
import br.all.application.study.update.interfaces.UpdateStudyReviewService
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.StudyReview

class UpdateStudyReviewServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: ResearcherCredentialsService,
) : UpdateStudyReviewService {

    override fun updateFromStudy(presenter: UpdateStudyReviewPresenter, request: UpdateStudyReviewService.RequestModel) {
        val researcherId = ResearcherId(request.researcherId)
        val systematicStudyId = SystematicStudyId(request.systematicStudyId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        if(presenter.isDone()) return

        val studyId = request.studyReviewId
        if(studyReviewRepository.findById(request.systematicStudyId, studyId) == null){
            presenter.prepareFailView(EntityNotFoundException("Study of id $studyId not found"))
            return }

        val studyReview = StudyReview.fromStudyUpdateRequestModel(studyId, request)

        studyReviewRepository.saveOrUpdate(studyReview.toDto())
        presenter.prepareSuccessView(UpdateStudyReviewService.ResponseModel(request.researcherId,
            request.systematicStudyId, studyId))
    }
}


