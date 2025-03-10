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
import java.util.UUID

class MarkAsDuplicatedServiceImpl(
    val systematicStudyRepository: SystematicStudyRepository,
    val studyReviewRepository: StudyReviewRepository,
    val credentialsService: CredentialsService,
) : MarkAsDuplicatedService {

    override fun markAsDuplicated(
        presenter: MarkAsDuplicatedPresenter,
        request: MarkAsDuplicatedService.RequestModel
    ) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        val referenceStudyDto = studyReviewRepository.findById(request.systematicStudyId, request.referenceStudyId)
        if (referenceStudyDto == null) {
            val message = "Reference study review with id ${request.referenceStudyId} not found."
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }
        val referenceStudyReview = StudyReview.fromDto(referenceStudyDto)

        request.duplicatedStudyIds.forEach { duplicatedStudyId ->
            val duplicatedStudyDto = studyReviewRepository.findById(request.systematicStudyId, duplicatedStudyId)
            if (duplicatedStudyDto == null) {
                val message = "Study review to be marked as duplicated with id $duplicatedStudyId not found."
                presenter.prepareFailView(EntityNotFoundException(message))
                return
            }
            val duplicatedStudyReview = StudyReview.fromDto(duplicatedStudyDto)
            duplicatedStudyReview.markAsDuplicated(referenceStudyReview)

            studyReviewRepository.saveOrUpdate(duplicatedStudyReview.toDto())
        }

        val response = ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            duplicatedStudies = request.duplicatedStudyIds
        )
        presenter.prepareSuccessView(response)
    }
}
