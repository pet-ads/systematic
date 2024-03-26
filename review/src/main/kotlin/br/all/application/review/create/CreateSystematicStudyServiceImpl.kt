package br.all.application.review.create

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.toDto
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.create.CreateSystematicStudyService.RequestModel
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.protocol.Protocol
import br.all.domain.model.researcher.toResearcherId
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.toSystematicStudyId
import br.all.domain.services.UuidGeneratorService

class CreateSystematicStudyServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val protocolRepository: ProtocolRepository,
    private val uuidGeneratorService: UuidGeneratorService,
    private val credentialsService: ResearcherCredentialsService,
) : CreateSystematicStudyService {
    override fun create(presenter: CreateSystematicStudyPresenter, request: RequestModel) {
        val (researcher) = request
        PreconditionChecker(systematicStudyRepository, credentialsService).also {
            it.prepareIfUnauthenticatedOrUnauthorized(presenter, researcher.toResearcherId())
        }
        if (presenter.isDone()) return

        val id = uuidGeneratorService.next()
        SystematicStudy.fromRequestModel(id, request).also {
            systematicStudyRepository.saveOrUpdate(it.toDto())
        }

        val protocol = Protocol.write(id.toSystematicStudyId(), emptySet())
            .build()
        protocolRepository.saveOrUpdate(protocol.toDto())

        presenter.prepareSuccessView(ResponseModel(researcher, id))
    }
}
