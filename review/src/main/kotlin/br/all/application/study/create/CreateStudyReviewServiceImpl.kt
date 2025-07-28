package br.all.application.study.create

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.create.CreateStudyReviewService.RequestModel
import br.all.application.study.create.CreateStudyReviewService.ResponseModel
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromStudyRequestModel
import br.all.application.study.repository.toDto
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.StudyReview
import br.all.domain.services.IdGeneratorService

class CreateStudyReviewServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
    private val idGenerator: IdGeneratorService,
    private val collaborationRepository: CollaborationRepository
) : CreateStudyReviewService {

    override fun createFromStudy(presenter: CreateStudyReviewPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)

        if(presenter.isDone()) return

        val studyId = idGenerator.next()
        val studyReview = StudyReview.fromStudyRequestModel(studyId, request)

        studyReviewRepository.saveOrUpdate(studyReview.toDto())
        presenter.prepareSuccessView(ResponseModel(request.userId, request.systematicStudyId, studyId))
    }
}


