package br.all.application.review.create

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDto
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.toDto
import br.all.application.user.CredentialsService
import br.all.application.review.create.CreateSystematicStudyService.RequestModel
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.application.shared.presenter.prepareIfUnauthorized
import br.all.domain.model.collaboration.Collaboration
import br.all.domain.model.collaboration.CollaborationId
import br.all.domain.model.collaboration.CollaborationPermission
import br.all.domain.model.collaboration.toCollaborationId
import br.all.domain.model.protocol.Protocol
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.toSystematicStudyId
import br.all.domain.model.user.ResearcherId
import br.all.domain.services.UuidGeneratorService

class CreateSystematicStudyServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val protocolRepository: ProtocolRepository,
    private val uuidGeneratorService: UuidGeneratorService,
    private val credentialsService: CredentialsService,
    private val collaborationRepository: CollaborationRepository,
) : CreateSystematicStudyService {

    override fun create(presenter: CreateSystematicStudyPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()
        presenter.prepareIfUnauthorized(user)

        if (presenter.isDone()) return

        val generatedId = uuidGeneratorService.next()
        val collabId = uuidGeneratorService.next()
        
        val ownerCollaboration = Collaboration(
            collabId.toCollaborationId(),
            generatedId.toSystematicStudyId(),
            user!!.id as ResearcherId,
            permissions = setOf(CollaborationPermission.VIEW, CollaborationPermission.EDIT, CollaborationPermission.REVIEW_STUDIES)
        )
        collaborationRepository.saveOrUpdateCollaboration(ownerCollaboration.toDto())
        
        val systematicStudy = SystematicStudy.fromRequestModel(generatedId, request, ownerCollaboration.id as CollaborationId)
        systematicStudyRepository.saveOrUpdate(systematicStudy.toDto())

        val protocol = Protocol.write(generatedId.toSystematicStudyId(), emptySet()).build()
        protocolRepository.saveOrUpdate(protocol.toDto())

        presenter.prepareSuccessView(ResponseModel(user.id.value(), generatedId))
    }
}
