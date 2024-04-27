package br.all.application.review.create

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.toDto
import br.all.application.researcher.CredentialsService
import br.all.application.review.create.CreateSystematicStudyService.RequestModel
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.application.shared.presenter.prepareIfUnauthorized
import br.all.domain.model.protocol.Protocol
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.toSystematicStudyId
import br.all.domain.services.UuidGeneratorService

class CreateSystematicStudyServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val protocolRepository: ProtocolRepository,
    private val uuidGeneratorService: UuidGeneratorService,
    private val credentialsService: CredentialsService,
) : CreateSystematicStudyService {

    override fun create(presenter: CreateSystematicStudyPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()
        presenter.prepareIfUnauthorized(user)

        if (presenter.isDone()) return

        val generatedId = uuidGeneratorService.next()
        val systematicStudy = SystematicStudy.fromRequestModel(generatedId, request)
        systematicStudyRepository.saveOrUpdate(systematicStudy.toDto())

        val protocol = Protocol.write(generatedId.toSystematicStudyId(), emptySet()).build()
        protocolRepository.saveOrUpdate(protocol.toDto())

        presenter.prepareSuccessView(ResponseModel(user!!.id.value(), generatedId))
    }
}
