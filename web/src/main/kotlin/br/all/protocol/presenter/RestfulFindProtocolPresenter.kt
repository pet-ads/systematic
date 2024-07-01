package br.all.protocol.presenter

import br.all.application.protocol.find.FindProtocolPresenter
import br.all.application.protocol.find.FindProtocolService.ResponseModel
import br.all.application.protocol.repository.ProtocolDto
import br.all.protocol.controller.ProtocolController
import br.all.protocol.requests.PutRequest
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindProtocolPresenter: FindProtocolPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val (researcher, systematicStudy, content) = response
        val viewModel = ViewModel(researcher, systematicStudy, content)

        val link = linkTo<ProtocolController> { findById(systematicStudy) }.withSelfRel()

        val putProtocol = linkTo<ProtocolController> {
            putProtocol(
                response.systematicStudyId,
                request = PutRequest()
            )
        }.withRel("update-protocol")

        viewModel.add(link, putProtocol)
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
