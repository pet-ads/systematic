package br.all.application.study.update.implementation

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.update.interfaces.MarkAsDuplicatedPresenter
import br.all.application.study.update.interfaces.MarkAsDuplicatedService
import br.all.application.study.update.interfaces.MarkAsDuplicatedService.ResponseModel
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.StudyReview

class MarkAsDuplicatedServiceImpl (
    val systematicStudyRepository: SystematicStudyRepository,
    val studyReviewRepository: StudyReviewRepository,
    val credentialsService: CredentialsService,
) : MarkAsDuplicatedService{

    override fun markAsDuplicated(presenter: MarkAsDuplicatedPresenter, request: MarkAsDuplicatedService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

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
            request.userId,
            request.systematicStudyId,
            request.studyReviewDestination,
            request.studyReviewSource
        )
        presenter.prepareSuccessView(response)
    }
}