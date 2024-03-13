package br.all.application.search.find.service

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.find.presenter.FindAllSearchSessionsPresenter
import br.all.application.search.find.service.FindAllSearchSessionsService.RequestModel
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId

class FindAllSearchSessionsServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val searchSessionRepository: SearchSessionRepository,
    private val credentialsService: ResearcherCredentialsService,
): FindAllSearchSessionsService {
    override fun findAllSearchSessions (presenter: FindAllSearchSessionsPresenter, request: RequestModel) {
        val researcherId = ResearcherId(request.researcherId)
        val systematicStudyId = SystematicStudyId(request.systematicStudyId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        if(presenter.isDone()) return

        val searchSessions = searchSessionRepository.findAllFromSystematicStudy(request.systematicStudyId)
        presenter.prepareSuccessView(FindAllSearchSessionsService.ResponseModel(request.researcherId, request.systematicStudyId, searchSessions))
    }
}