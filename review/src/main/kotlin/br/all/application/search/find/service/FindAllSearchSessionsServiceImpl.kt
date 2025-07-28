package br.all.application.search.find.service

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.search.find.presenter.FindAllSearchSessionsPresenter
import br.all.application.search.find.service.FindAllSearchSessionsService.RequestModel
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy

class FindAllSearchSessionsServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val searchSessionRepository: SearchSessionRepository,
    private val credentialsService: CredentialsService,
    private val collaborationRepository: CollaborationRepository,
): FindAllSearchSessionsService {
    override fun findAllSearchSessions (presenter: FindAllSearchSessionsPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)

        if(presenter.isDone()) return

        val searchSessions = searchSessionRepository.findAllFromSystematicStudy(request.systematicStudyId)
        presenter.prepareSuccessView(FindAllSearchSessionsService.ResponseModel(
            request.userId, request.systematicStudyId, searchSessions
        ))
    }
}