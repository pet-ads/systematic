package br.all.application.protocol.find

import br.all.application.protocol.find.FindProtocolService.RequestModel
import br.all.application.protocol.find.FindProtocolService.ResponseModel
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.researcher.toResearcherId
import br.all.domain.model.review.toSystematicStudyId

class FindProtocolServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService,
) : FindProtocolService {
    override fun findById(presenter: FindProtocolPresenter, request: RequestModel) {
        val (researcher, systematicStudy) = request
        PreconditionChecker(systematicStudyRepository, credentialsService).also {
            it.prepareIfViolatesPreconditions(presenter, researcher.toResearcherId(), systematicStudy.toSystematicStudyId())
        }
        if (presenter.isDone()) return

        val dto = protocolRepository.findById(systematicStudy)

        if (dto == null) {
            val message = "The protocol for systematic study $systematicStudy hasn't been written yet!"
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        ResponseModel(researcher, systematicStudy, dto).also {
            presenter.prepareSuccessView(it)
        }
    }
}