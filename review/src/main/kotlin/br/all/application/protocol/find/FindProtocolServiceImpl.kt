package br.all.application.protocol.find

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.protocol.find.FindProtocolService.RequestModel
import br.all.application.protocol.find.FindProtocolService.ResponseModel
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy

class FindProtocolServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
    private val collaborationRepository: CollaborationRepository
) : FindProtocolService {
    override fun findById(presenter: FindProtocolPresenter, request: RequestModel) {
        val userId = request.userId
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyId = request.systematicStudyId
        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)

        if (presenter.isDone()) return

        val dto = protocolRepository.findById(systematicStudyId)

        if (dto == null) {
            val message = "The protocol for systematic study $systematicStudy hasn't been written yet!"
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        ResponseModel(userId, systematicStudyId, dto).also {
            presenter.prepareSuccessView(it)
        }
    }
}