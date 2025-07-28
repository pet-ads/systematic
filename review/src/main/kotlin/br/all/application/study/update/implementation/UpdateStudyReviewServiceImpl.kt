package br.all.application.study.update.implementation

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromStudyUpdateRequestModel
import br.all.application.study.repository.toDto
import br.all.application.study.update.interfaces.UpdateStudyReviewPresenter
import br.all.application.study.update.interfaces.UpdateStudyReviewService
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.StudyReview

class UpdateStudyReviewServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
) : UpdateStudyReviewService {

    override fun updateFromStudy(presenter: UpdateStudyReviewPresenter, request: UpdateStudyReviewService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if(presenter.isDone()) return

        val studyId = request.studyReviewId
        if(studyReviewRepository.findById(request.systematicStudyId, studyId) == null){
            presenter.prepareFailView(EntityNotFoundException("Study of id $studyId not found"))
            return }

        val studyReview = StudyReview.fromStudyUpdateRequestModel(studyId, request)

        studyReviewRepository.saveOrUpdate(studyReview.toDto())
        presenter.prepareSuccessView(UpdateStudyReviewService.ResponseModel(request.userId,
            request.systematicStudyId, studyId))
    }
}


