package br.all.protocol.presenter

import br.all.application.protocol.find.FindProtocolPresenter
import br.all.application.protocol.find.FindProtocolService.ResponseModel
import br.all.application.protocol.repository.ProtocolDto
import br.all.protocol.controller.ProtocolController
import br.all.protocol.requests.PutRequest
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindProtocolPresenter(
    private val linksFactory: LinksFactory
): FindProtocolPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val (researcher, systematicStudy, content) = response
        val viewModel = ViewModel(researcher, systematicStudy, content)

        val findProtocol = linksFactory.findProtocol(systematicStudy)
        val updateProtocol = linksFactory.updateProtocol(systematicStudy)

        viewModel.add(findProtocol, updateProtocol)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val content: ProtocolDto,
    ) : RepresentationModel<ViewModel>()
}
