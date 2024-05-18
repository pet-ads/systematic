package br.all.application.search.find.service

import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.search.find.presenter.FindAllSearchSessionsBySourcePresenter
import br.all.application.search.find.service.FindAllSearchSessionsBySourceService.*
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.SystematicStudyId
//TODO finish
class FindAllSearchSessionsBySourceServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val searchSessionRepository: SearchSessionRepository,
    private val credentialsService: CredentialsService,
): FindAllSearchSessionsBySourceService{
    override fun findAllSessionsBySource (
        presenter: FindAllSearchSessionsBySourcePresenter, request: RequestModel
    ) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if(presenter.isDone()) return

        val searchSessions = searchSessionRepository.findSearchSessionsBySource(
            request.systematicStudyId, request.source
        )
        presenter.prepareSuccessView(ResponseModel(
            request.userId, request.systematicStudyId, request.source, searchSessions
        ))
    }
}