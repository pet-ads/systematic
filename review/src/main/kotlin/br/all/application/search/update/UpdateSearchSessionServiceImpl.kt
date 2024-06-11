package br.all.application.search.update

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.search.repository.fromDto
import br.all.application.search.repository.toDto
import br.all.application.search.update.UpdateSearchSessionService.RequestModel
import br.all.application.search.update.UpdateSearchSessionService.ResponseModel
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.protocol.toSearchSource
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.search.SearchSession

class UpdateSearchSessionServiceImpl (
    private val systematicStudyRepository: SystematicStudyRepository,
    private val searchSessionRepository: SearchSessionRepository,
    private val credentialsService: CredentialsService,
) : UpdateSearchSessionService {
    override fun updateSession(presenter: UpdateSearchSessionPresenter, request: RequestModel
    ) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)


        if(presenter.isDone()) return

        if (searchSessionRepository.existsById(request.sessionId)) {

            val dto = searchSessionRepository.findById(request.sessionId) ?: return

            val updated = SearchSession.fromDto(dto).apply {
                searchString = request.searchString ?: searchString
                additionalInfo = request.additionalInfo ?: additionalInfo
                source = request.source?.toSearchSource() ?: source
            }.toDto()

            if (updated != dto) searchSessionRepository.saveOrUpdate(updated)

            presenter.prepareSuccessView(ResponseModel(request.userId, request.systematicStudyId, request.sessionId))

        } else {
            val message = "There is no search session of id ${request.sessionId}"
            presenter.prepareFailView(EntityNotFoundException(message))
        }
    }
}