package br.all.application.study.find.service

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.find.presenter.FindAllStudyReviewsBySourcePresenter
import br.all.application.study.find.service.FindAllStudyReviewsBySourceService.RequestModel
import br.all.application.study.find.service.FindAllStudyReviewsBySourceService.ResponseModel
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.review.SystematicStudy

class FindAllStudyReviewsBySourceServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
   // private val protocolRepository: ProtocolRepository,
    private val credentialsService: CredentialsService,
    private val collaborationRepository: CollaborationRepository,
) : FindAllStudyReviewsBySourceService {

    override fun findAllFromSearchSession(presenter: FindAllStudyReviewsBySourcePresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)

        if(presenter.isDone()) return

        val searchSource = SearchSource(request.searchSource)

//        val protocolDto = protocolRepository.findBySystematicStudy(systematicStudyId.value)
//        val hasSource = protocolDto?.informationSources?.contains(searchSource.id) ?: false
//
//        if (!hasSource) {
//            val message = "Protocol ID $systematicStudyId does not contain ${searchSource.id} as a search source"
//            presenter.prepareFailView(NoSuchElementException(message))
//        }

        if (presenter.isDone()) return

        val studyReviews = studyReviewRepository.findAllBySource(request.systematicStudyId, searchSource.toString())
        presenter.prepareSuccessView(
            ResponseModel(
                request.userId, request.systematicStudyId,
                request.searchSource, studyReviews
            )
        )
    }
}

