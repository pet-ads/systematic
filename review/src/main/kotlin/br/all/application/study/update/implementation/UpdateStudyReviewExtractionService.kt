package br.all.application.study.update.implementation

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.PreconditionChecker
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.RequestModel
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.ResponseModel
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.ReviewId
import br.all.domain.model.study.StudyReview

class UpdateStudyReviewExtractionService (
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: ResearcherCredentialsService,
) : UpdateStudyReviewStatusService {

    override fun changeStatus(presenter: UpdateStudyReviewStatusPresenter, request: RequestModel){
        val researcherId = ResearcherId(request.researcherId)
        val reviewId = ReviewId(request.reviewId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, reviewId)

        if(presenter.isDone()) return

        val studyReviewDto = studyReviewRepository.findById(request.reviewId, request.studyReviewId)
        if(studyReviewDto == null) {
            presenter.prepareFailView(EntityNotFoundException("Study review of id ${request.reviewId} not found."))
            return
        }

        val studyReview = StudyReview.fromDto(studyReviewDto)
        when(request.status.uppercase()){
            "UNCLASSIFIED" -> studyReview.unclassifyInExtraction()
            "DUPLICATED" -> studyReview.markAsDuplicate()
            "INCLUDED" -> studyReview.includeInExtraction()
            "EXCLUDED" -> studyReview.excludeInExtraction()
            else -> throw IllegalArgumentException("Unknown study review status: ${request.status}.")
        }
        studyReviewRepository.saveOrUpdate(studyReview.toDto())
        presenter.prepareSuccessView(ResponseModel(request.researcherId, request.reviewId, request.studyReviewId))
    }
}
