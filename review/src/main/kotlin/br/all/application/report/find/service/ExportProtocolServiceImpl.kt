package br.all.application.report.find.service

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.find.presenter.ExportProtocolPresenter
import br.all.application.user.CredentialsService
import java.util.*

class ExportProtocolServiceImpl(
    private val credentialsService: CredentialsService,
    private val protocolRepository: ProtocolRepository,
): ExportProtocolService {
    override fun exportProtocol(presenter: ExportProtocolPresenter, request: ExportProtocolService.RequestModel) {
        val response = ExportProtocolService.ResponseModel(
            userId = UUID.randomUUID(),
            systematicStudyId = UUID.randomUUID(),
            formattedProtocol = "formattedProtocol"
        )

        presenter.prepareSuccessView(response)
    }
}