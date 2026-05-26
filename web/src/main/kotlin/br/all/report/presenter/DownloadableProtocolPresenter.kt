package br.all.report.presenter

import br.all.application.report.find.presenter.ExportProtocolPresenter
import br.all.application.report.find.service.ExportProtocolService
import br.all.shared.error.createErrorResponseFrom
import org.springframework.http.ResponseEntity

class DownloadableProtocolPresenter : ExportProtocolPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ExportProtocolService.ResponseModel) {
        responseEntity = buildDownloadResponse(
            format = response.format,
            systematicStudyId = response.systematicStudyId,
            prefix = "protocol",
            content = response.formattedProtocol
        )
    }

    override fun prepareFailView(throwable: Throwable) {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone() = responseEntity != null
}
