package br.all.application.study.update.implementation

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.researcher.repository.ResearcherRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.PreconditionChecker
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.update.interfaces.MarkAsDuplicatedPresenter
import br.all.application.study.update.interfaces.MarkAsDuplicatedService
import br.all.application.study.update.interfaces.MarkAsDuplicatedService.ResponseModel
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.StudyReview

class MarkAsDuplicatedServiceImpl (
    val systematicStudyRepository: SystematicStudyRepository,
    val studyReviewRepository: StudyReviewRepository,
    val credentialsService: ResearcherCredentialsService,
) : MarkAsDuplicatedService{

    override fun markAsDuplicated(presenter: MarkAsDuplicatedPresenter, request: MarkAsDuplicatedService.RequestModel) {
        val researcherId = ResearcherId(request.researcherId)
        val systematicStudyId = SystematicStudyId(request.systematicStudyId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        if (presenter.isDone()) return

        val sourceStudyDto = studyReviewRepository.findById(request.systematicStudyId, request.studyReviewSource)
        if(sourceStudyDto == null) {
            val message = "Study review source of id ${request.systematicStudyId} not found."
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        val destinationStudyDto = studyReviewRepository.findById(request.systematicStudyId, request.studyReviewDestination)
        if(destinationStudyDto == null) {
            val message = "Study review destination of id ${request.systematicStudyId} not found."
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        val sourceStudyReview = StudyReview.fromDto(sourceStudyDto)
        val destinationStudyReview = StudyReview.fromDto(destinationStudyDto)
        sourceStudyReview.markAsDuplicated(destinationStudyReview)

        studyReviewRepository.saveOrUpdate(sourceStudyReview.toDto())
        studyReviewRepository.saveOrUpdate(destinationStudyReview.toDto())

        val response = ResponseModel(
            request.researcherId,
            request.systematicStudyId,
            request.studyReviewDestination,
            request.studyReviewSource
        )

        presenter.prepareSuccessView(response)
    }


}