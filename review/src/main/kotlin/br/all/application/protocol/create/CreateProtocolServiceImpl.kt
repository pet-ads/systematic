package br.all.application.protocol.create

import br.all.application.protocol.create.CreateProtocolService.RequestModel
import br.all.application.protocol.create.CreateProtocolService.ResponseModel
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.fromRequestModel
import br.all.application.protocol.repository.toDto
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.protocol.Protocol
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId

class CreateProtocolServiceImpl(
    private val repository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService,
): CreateProtocolService {
    override fun create(presenter: CreateProtocolPresenter, request: RequestModel) {
        PreconditionChecker(systematicStudyRepository, credentialsService).also {
            val (researcher, systematicStudy) = request
            it.prepareIfViolatesPreconditions(presenter, ResearcherId(researcher), SystematicStudyId(systematicStudy))
        }
        if (presenter.isDone()) return

        val protocol = Protocol.fromRequestModel(request)
        repository.saveOrUpdate(protocol.toDto())

        ResponseModel(request.researcherId, request.systematicStudyId).also {
            presenter.prepareSuccessView(it)
        }
    }
}