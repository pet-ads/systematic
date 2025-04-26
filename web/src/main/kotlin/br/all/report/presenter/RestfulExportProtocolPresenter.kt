package br.all.report.presenter

import br.all.application.report.find.presenter.ExportProtocolPresenter
import br.all.application.report.find.service.ExportProtocolService
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulExportProtocolPresenter(
    private val linksFactory: LinksFactory
): ExportProtocolPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ExportProtocolService.ResponseModel) {
        val restfulResponse = ViewModel(
            response.userId,
            response.systematicStudyId,
            response.formattedProtocol,
        )

        val selfRef = linksFactory.exportProtocol(response.systematicStudyId, response.format)

        restfulResponse.add(selfRef)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val userId: UUID,
        val systematicStudyId: UUID,
        val formattedProtocol: String,
    ): RepresentationModel<ViewModel>()
}