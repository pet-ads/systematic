package br.all.application.search.delete

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy

class DeleteSearchSessionServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val searchSessionRepository: SearchSessionRepository,
    private val credentialsService: CredentialsService,
    private val collaborationRepository: CollaborationRepository
) : DeleteSearchSessionService {
    override fun delete(
        presenter: DeleteSearchSessionPresenter,
        request: DeleteSearchSessionService.RequestModel,
    ) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)

        if (presenter.isDone()) return

        val searchSessionDto = searchSessionRepository.findById(request.sessionId)
        
        if(searchSessionDto == null){
            presenter.prepareFailView(EntityNotFoundException("There is no search session with ID ${request.sessionId}"))
            return
        }
        
        searchSessionRepository.deleteById(searchSessionDto.id)
        presenter.prepareSuccessView(
            DeleteSearchSessionService.ResponseModel(
                userId = request.userId,
                systematicStudyId = request.systematicStudyId,
                sessionId = request.sessionId,
            ),
        )
    }
}
