package br.all.application.protocol.update

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.repository.copyUpdates
import br.all.application.protocol.repository.fromDto
import br.all.application.protocol.repository.toDto
import br.all.application.protocol.update.UpdateProtocolService.RequestModel
import br.all.application.protocol.update.UpdateProtocolService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.protocol.Protocol
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.toSystematicStudyId

class UpdateProtocolServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
) : UpdateProtocolService {
    override fun update(presenter: UpdateProtocolPresenter, request: RequestModel) {
        val userId = request.userId
        val user = credentialsService.loadCredentials(userId)?.toUser()

        val systematicStudyId = request.systematicStudyId
        val systematicStudyDto = systematicStudyRepository.findById(systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        val dto = protocolRepository.findById(systematicStudyId)
        val protocol = dto?.let { Protocol.fromDto(it) } ?: Protocol.write(
            systematicStudyId.toSystematicStudyId(),
            emptySet()
        ).build()
        protocol.copyUpdates(request)

        val updatedDto = protocol.toDto()
        if (updatedDto != dto) protocolRepository.saveOrUpdate(updatedDto)

        presenter.prepareSuccessView(ResponseModel(userId, systematicStudyId))
    }
}
