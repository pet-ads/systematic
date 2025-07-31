package br.all.application.study.update.implementation

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.update.interfaces.MarkAsDuplicatedPresenter
import br.all.application.study.update.interfaces.MarkAsDuplicatedService
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.StudyReview

class MarkAsDuplicatedServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
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
            presenter.prepareFailView(EntityNotFoundException("Reference study review with id ${request.referenceStudyId} not found."))
            return
        }
        val referenceStudyReview = StudyReview.fromDto(referenceStudyDto)

        val duplicates = request.duplicatedStudyIds.map { duplicateId ->
            val dupDto = studyReviewRepository.findById(request.systematicStudyId, duplicateId)
            if (dupDto == null) {
                presenter.prepareFailView(EntityNotFoundException("Study review to be marked as duplicated with id $duplicateId not found."))
                return
            }
            StudyReview.fromDto(dupDto)
        }

        referenceStudyReview.markAsDuplicated(duplicates)

        duplicates.forEach { duplicate ->
            studyReviewRepository.saveOrUpdate(duplicate.toDto())
        }
        studyReviewRepository.saveOrUpdate(referenceStudyReview.toDto())

        val response = MarkAsDuplicatedService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            duplicatedStudies = request.duplicatedStudyIds,
            referenceStudyId = request.referenceStudyId,
        )
        presenter.prepareSuccessView(response)
    }
}

