package br.all.protocol.presenter

import br.all.application.protocol.create.CreateProtocolPresenter
import br.all.application.protocol.create.CreateProtocolService
import br.all.protocol.controller.ProtocolController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulCreateProtocolPresenter: CreateProtocolPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: CreateProtocolService.ResponseModel) {
        val (researcher, systematicStudy) = response
        val viewModel = ViewModel(researcher, systematicStudy)

        val link = linkTo<ProtocolController> { findById(researcher, systematicStudy) }.withSelfRel()
        viewModel.add(link)

        responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity == null

    data class ViewModel(
        val researcher: UUID,
        val systematicStudy: UUID,
    ): RepresentationModel<ViewModel>()
}
