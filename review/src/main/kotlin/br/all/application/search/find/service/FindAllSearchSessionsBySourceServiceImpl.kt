package br.all.application.search.find.service

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.find.presenter.FindAllSearchSessionsBySourcePresenter
import br.all.application.search.find.service.FindAllSearchSessionsBySourceService.*
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId

class FindAllSearchSessionsBySourceServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val searchSessionRepository: SearchSessionRepository,
    private val credentialsService: ResearcherCredentialsService,
): FindAllSearchSessionsBySourceService{
    override fun findAllSessionsBySource (
        presenter: FindAllSearchSessionsBySourcePresenter, request: RequestModel
    ) {
        val researcherId = ResearcherId(request.researcherId)
        val systematicStudyId = SystematicStudyId(request.systematicStudyId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        if(presenter.isDone()) return

        val searchSessions = searchSessionRepository.findSearchSessionsBySource(
            request.systematicStudyId, request.searchSource
        )
        presenter.prepareSuccessView(ResponseModel(
            request.researcherId, request.systematicStudyId, request.searchSource, searchSessions
        ))
    }
}