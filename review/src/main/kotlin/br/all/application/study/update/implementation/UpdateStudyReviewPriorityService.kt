package br.all.application.study.update.implementation

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
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

class UpdateStudyReviewPriorityService(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
    private val collaborationRepository: CollaborationRepository,
) : UpdateStudyReviewStatusService {

    override fun changeStatus(presenter: UpdateStudyReviewStatusPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)

        if(presenter.isDone()) return

        for (studyId in request.studyReviewId) {
            val studyReviewDto = studyReviewRepository.findById(request.systematicStudyId, studyId)
            if(studyReviewDto == null) {
                presenter.prepareFailView(EntityNotFoundException("Study review of id ${request.systematicStudyId} not found."))
                return
            }

            val studyReview = StudyReview.fromDto(studyReviewDto).apply {
                readingPriority = ReadingPriority.valueOf(request.status)
            }
            studyReviewRepository.saveOrUpdate(studyReview.toDto())
        }

        presenter.prepareSuccessView(ResponseModel(request.userId, request.systematicStudyId, request.studyReviewId))
    }
}