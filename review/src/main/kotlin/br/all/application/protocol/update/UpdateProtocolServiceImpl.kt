package br.all.application.protocol.update

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.copyUpdates
import br.all.application.protocol.repository.fromDto
import br.all.application.protocol.repository.toDto
import br.all.application.protocol.update.UpdateProtocolService.RequestModel
import br.all.application.protocol.update.UpdateProtocolService.ResponseModel
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.protocol.Protocol
import br.all.domain.model.researcher.toResearcherId
import br.all.domain.model.review.toSystematicStudyId

class UpdateProtocolServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService,
) : UpdateProtocolService {
    override fun update(presenter: UpdateProtocolPresenter, request: RequestModel) {
        val (researcher, systematicStudy) = request

        PreconditionChecker(systematicStudyRepository, credentialsService).also {
            it.prepareIfViolatesPreconditions(presenter, researcher.toResearcherId(), systematicStudy.toSystematicStudyId())
        }
        if (presenter.isDone()) return

        val dto = protocolRepository.findById(systematicStudy)
        val protocol = dto?.let { Protocol.fromDto(it) } ?: Protocol.write(
            systematicStudy.toSystematicStudyId(),
            emptySet()
        ).build()
        protocol.copyUpdates(request)

        val updatedDto = protocol.toDto()
        if (updatedDto != dto) protocolRepository.saveOrUpdate(updatedDto)

        presenter.prepareSuccessView(ResponseModel(researcher, systematicStudy))
    }
}
