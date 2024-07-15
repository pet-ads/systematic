package br.all.protocol.presenter

import br.all.application.protocol.update.UpdateProtocolPresenter
import br.all.application.protocol.update.UpdateProtocolService
import br.all.protocol.controller.ProtocolController
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulUpdateProtocolPresenter(
    private val linksFactory: LinksFactory
): UpdateProtocolPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: UpdateProtocolService.ResponseModel) {
        val (researcher, systematicStudy) = response
        val viewModel = ViewModel(researcher, systematicStudy)

        val findProtocol = linksFactory.findProtocol(systematicStudy)

        viewModel.add(findProtocol)

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
    ): RepresentationModel<ViewModel>()
}
