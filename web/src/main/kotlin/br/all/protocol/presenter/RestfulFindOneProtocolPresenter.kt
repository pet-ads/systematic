package br.all.protocol.presenter

import br.all.application.protocol.find.FindOneProtocolPresenter
import br.all.application.protocol.find.FindOneProtocolService.ResponseModel
import br.all.application.protocol.repository.ProtocolDto
import br.all.protocol.controller.ProtocolController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindOneProtocolPresenter: FindOneProtocolPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val (researcher, systematicStudy, content) = response
        val viewModel = ViewModel(researcher, systematicStudy, content)

        val link = linkTo<ProtocolController> { findById(researcher, systematicStudy) }.withSelfRel()

//        TODO: protocol does not have POST endpoint anymore. Maybe you could replace this one by the PUT endpoint.
//        val postProtocol = linkTo<ProtocolController> {
//            postProtocol(
//                response.researcherId,
//                response.systematicStudyId,
//                request = ProtocolRequest()
//            )
//        }.withRel("postProtocol")
//        viewModel.add(link,postProtocol)

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val content: ProtocolDto,
    ): RepresentationModel<ViewModel>()
}
