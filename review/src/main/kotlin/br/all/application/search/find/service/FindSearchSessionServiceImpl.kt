package br.all.application.search.find.service

import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.search.find.presenter.FindSearchSessionPresenter
import br.all.application.search.find.service.FindSearchSessionService.RequestModel
import br.all.application.search.find.service.FindSearchSessionService.ResponseModel
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy

class FindSearchSessionServiceImpl (
    private val systematicStudyRepository: SystematicStudyRepository,
    private val searchSessionRepository: SearchSessionRepository,
    private val credentialsService: CredentialsService,
) : FindSearchSessionService {

    override fun findOneSession(presenter: FindSearchSessionPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)


        if(presenter.isDone()) return

        val searchSession = searchSessionRepository.findById(request.sessionId)

        if (searchSession == null){
            val message = "There is no search session of id ${request.sessionId}"
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }
        presenter.prepareSuccessView((ResponseModel(request.userId, searchSession)))
    }
}