package br.all.application.search.update

import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.search.repository.fromDto
import br.all.application.search.repository.toDto
import br.all.application.search.update.UpdateSearchSessionService.RequestModel
import br.all.application.search.update.UpdateSearchSessionService.ResponseModel
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UniquenessViolationException
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.protocol.toSearchSource
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSession

class UpdateSearchSessionServiceImpl (
    private val systematicStudyRepository: SystematicStudyRepository,
    private val searchSessionRepository: SearchSessionRepository,
    private val credentialsService: ResearcherCredentialsService,
) : UpdateSearchSessionService {
    override fun updateSession(presenter: UpdateSearchSessionPresenter, request: RequestModel
    ) {
        val (researcher, systematicStudy, session) = request
        PreconditionChecker(systematicStudyRepository, credentialsService).also {
            it.prepareIfViolatesPreconditions(presenter, ResearcherId(researcher), SystematicStudyId(systematicStudy))
        }

        if(presenter.isDone()) return

        if (searchSessionRepository.existsById(session)) {

            val dto = searchSessionRepository.findById(session) ?: return

            val updated = SearchSession.fromDto(dto).apply {
                searchString = request.searchString ?: searchString
                additionalInfo = request.additionalInfo ?: additionalInfo
                source = request.source?.toSearchSource() ?: source
            }.toDto()

            if (updated != dto) searchSessionRepository.saveOrUpdate(updated)

            presenter.prepareSuccessView(ResponseModel(researcher, systematicStudy, session))

        } else {
            val message = "There is no search session of id ${request.sessionId}"
            presenter.prepareFailView(EntityNotFoundException(message))
        }
    }
}