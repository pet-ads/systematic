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
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.ExtractionStatus
import br.all.domain.model.study.StudyReview

class UpdateStudyReviewExtractionService(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: ResearcherCredentialsService,
) : UpdateStudyReviewStatusService {

    override fun changeStatus(presenter: UpdateStudyReviewStatusPresenter, request: RequestModel) {
        val researcherId = ResearcherId(request.researcherId)
        val systematicStudyId = SystematicStudyId(request.systematicStudyId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        if (presenter.isDone()) return

        val studyReviewDto = studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId)
        if (studyReviewDto == null) {
            presenter.prepareFailView(EntityNotFoundException("Study review of id ${request.systematicStudyId} not found."))
            return
        }

        val newStatus = request.status.uppercase()
        if (newStatus == "DUPLICATED") {
            val message = "Duplication request must indicate the duplicate study. Please use the proper feature."
            presenter.prepareFailView(IllegalArgumentException(message))
            return
        }

        val studyReview = StudyReview.fromDto(studyReviewDto)
        when (newStatus) {
            "UNCLASSIFIED" -> studyReview.declassifyInExtraction()
            "INCLUDED" -> studyReview.includeInExtraction()
            "EXCLUDED" -> studyReview.excludeInExtraction()
            else -> throw IllegalArgumentException("Unknown study review status: ${request.status}.")
        }
        studyReviewRepository.saveOrUpdate(studyReview.toDto())
        presenter.prepareSuccessView(
            ResponseModel(
                request.researcherId,
                request.systematicStudyId,
                request.studyReviewId
            )
        )
    }
}
