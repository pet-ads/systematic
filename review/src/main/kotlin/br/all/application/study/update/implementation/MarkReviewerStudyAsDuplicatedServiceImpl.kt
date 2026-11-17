package br.all.application.study.update.implementation

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.access.StudyReviewAccess
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.update.interfaces.MarkAsDuplicatedPresenter
import br.all.application.study.update.interfaces.MarkAsDuplicatedService
import br.all.application.study.update.interfaces.MarkReviewerStudyAsDuplicatedService
import br.all.application.study.update.interfaces.MarkReviewerStudyAsDuplicatedService.RequestModel
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.StudyReview
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.exception.EntityNotFoundException

class MarkReviewerStudyAsDuplicatedServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    studyReviewRepository: StudyReviewRepository,
    reviewerStudyRepository: ReviewerStudyRepository,
    private val credentialsService: CredentialsService,
) : MarkReviewerStudyAsDuplicatedService {

    private val studyReviewAccess = StudyReviewAccess(studyReviewRepository, reviewerStudyRepository)

    override fun markAsDuplicated(presenter: MarkAsDuplicatedPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        // o revisor só mexe nas próprias cópias
        studyReviewAccess.validate(request.userId, request.reviewerId, request.stage)?.let {
            presenter.prepareFailView(it)
            return
        }

        val duplicatedIds = request.duplicatedStudyIds.distinct()
        if (request.referenceStudyId in duplicatedIds) {
            presenter.prepareFailView(IllegalArgumentException("A study can not be marked as duplicate of itself."))
            return
        }

        val referenceTarget = studyReviewAccess.open(
            request.systematicStudyId, request.referenceStudyId, request.reviewerId, request.stage
        )
        if (referenceTarget == null) {
            presenter.prepareFailView(notAssigned("Reference study review", request.referenceStudyId, request))
            return
        }

        val duplicateTargets = mutableListOf<StudyReviewAccess.Target>()
        for (duplicateId in duplicatedIds) {
            val target = studyReviewAccess.open(
                request.systematicStudyId, duplicateId, request.reviewerId, request.stage
            )
            if (target == null) {
                presenter.prepareFailView(notAssigned("Study review to be marked as duplicated", duplicateId, request))
                return
            }
            duplicateTargets += target
        }

        val reference = StudyReview.fromDto(referenceTarget.dto)
        val duplicates = duplicateTargets.map { StudyReview.fromDto(it.dto) }

        when (request.stage) {
            StudyReviewStage.SELECTION -> reference.markAsDuplicatedInSelection(duplicates)
            StudyReviewStage.EXTRACTION -> reference.markAsDuplicatedInExtraction(duplicates)
        }

        duplicates.forEachIndexed { index, duplicate -> duplicateTargets[index].save(duplicate.toDto()) }
        referenceTarget.save(reference.toDto())

        presenter.prepareSuccessView(
            MarkAsDuplicatedService.ResponseModel(
                userId = request.userId,
                systematicStudyId = request.systematicStudyId,
                referenceStudyId = request.referenceStudyId,
                duplicatedStudies = request.duplicatedStudyIds,
            )
        )
    }

    private fun notAssigned(description: String, studyId: Long, request: RequestModel) =
        EntityNotFoundException(
            "$description with id $studyId is not assigned to reviewer ${request.reviewerId} in stage ${request.stage}."
        )
}