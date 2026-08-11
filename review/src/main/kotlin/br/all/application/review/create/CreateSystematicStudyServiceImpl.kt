package br.all.application.review.create

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.toDto
import br.all.application.user.CredentialsService
import br.all.application.review.create.CreateSystematicStudyService.RequestModel
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel
import br.all.application.collaborator.repository.CollaboratorRepository
import br.all.application.collaborator.repository.toDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.domain.model.protocol.Protocol
import br.all.domain.model.collaborator.Collaborator
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.review.toSystematicStudyId
import br.all.domain.services.UuidGeneratorService
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UnauthenticatedUserException
import br.all.domain.shared.exception.UnauthorizedUserException
import br.all.domain.shared.user.Email
import br.all.domain.shared.user.ResearcherId
import br.all.domain.shared.user.Role
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateSystematicStudyServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val protocolRepository: ProtocolRepository,
    private val uuidGeneratorService: UuidGeneratorService,
    private val credentialsService: CredentialsService,
    private val collaboratorRepository: CollaboratorRepository,
) : CreateSystematicStudyService {
    
    @Transactional
    override fun create(presenter: CreateSystematicStudyPresenter, request: RequestModel) {
        val userCredentials = credentialsService.loadEnabledCredentialsById(request.userId)
        if (userCredentials == null) {
            presenter.prepareFailView(
                EntityNotFoundException("User not found")
            )
        }

        userCredentials?.isEnabled?.let {
            if (!it) {
                presenter.prepareFailView(
                    UnauthorizedUserException("Please confirm your email to activate your account.")
                )
            }
        }

        if (userCredentials == null) {
            presenter.prepareFailView(UnauthenticatedUserException("Current user is not authenticated."))
        }

        if (presenter.isDone()) return

        val generatedId = uuidGeneratorService.next()
        val systematicStudy = SystematicStudy.fromRequestModel(generatedId, request)

        val collaborator = Collaborator(
            ResearcherId(request.userId),
            SystematicStudyId(generatedId),
            userCredentials!!.name,
            Email(userCredentials.email),
            Role.OWNER)

        collaboratorRepository.saveOrUpdate(collaborator.toDto())
        systematicStudyRepository.saveOrUpdate(systematicStudy.toDto())

        val protocol = Protocol.write(generatedId.toSystematicStudyId(), emptySet()).build()
        protocolRepository.saveOrUpdate(protocol.toDto())

        presenter.prepareSuccessView(ResponseModel(userCredentials.id, generatedId))
    }
}
