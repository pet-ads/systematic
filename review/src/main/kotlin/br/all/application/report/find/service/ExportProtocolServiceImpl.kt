package br.all.application.report.find.service

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.find.presenter.ExportProtocolPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.services.FormatterFactoryService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*

class ExportProtocolServiceImpl(
    private val credentialsService: CredentialsService,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val formatterFactoryService: FormatterFactoryService,
    private val protocolRepository: ProtocolRepository,
): ExportProtocolService {
    override fun exportProtocol(presenter: ExportProtocolPresenter, request: ExportProtocolService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if(presenter.isDone()) return

        val protocolDto = protocolRepository.findById(request.systematicStudyId)!!

        val formattedProtocol = formatterFactoryService.format(
            request.format,
            protocolDto,
        )

        val response = ExportProtocolService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            format = request.format,
            formattedProtocol = formattedProtocol,
        )

        presenter.prepareSuccessView(response)
    }
}