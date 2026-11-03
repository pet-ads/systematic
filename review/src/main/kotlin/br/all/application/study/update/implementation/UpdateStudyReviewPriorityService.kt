package br.all.application.study.update.implementation

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.access.StudyReviewAccess
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusPresenter
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.RequestModel
import br.all.application.study.update.interfaces.UpdateStudyReviewStatusService.ResponseModel
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.ReadingPriority
import br.all.domain.model.study.StudyReview
import br.all.domain.shared.exception.EntityNotFoundException

class UpdateStudyReviewPriorityService(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
    reviewerStudyRepository: ReviewerStudyRepository? = null,
) : UpdateStudyReviewStatusService {

    private val studyReviewAccess = StudyReviewAccess(studyReviewRepository, reviewerStudyRepository)

    override fun changeStatus(presenter: UpdateStudyReviewStatusPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if(presenter.isDone()) return

        studyReviewAccess.validate(request.userId, request.reviewerId, request.stage)?.let {
            presenter.prepareFailView(it)
            return
        }

        for (studyId in request.studyReviewId) {
            val target = studyReviewAccess.open(request.systematicStudyId, studyId, request.reviewerId, request.stage)
            if(target == null) {
                presenter.prepareFailView(EntityNotFoundException("Study review of id ${request.systematicStudyId} not found."))
                return
            }

            val studyReview = StudyReview.fromDto(target.dto).apply {
                readingPriority = ReadingPriority.valueOf(request.status)
            }
            target.save(studyReview.toDto())
        }

        presenter.prepareSuccessView(ResponseModel(request.userId, request.systematicStudyId, request.studyReviewId))
    }
}