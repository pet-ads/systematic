package br.all.protocol.presenter

import br.all.application.protocol.update.UpdateProtocolPresenter
import br.all.application.protocol.update.UpdateProtocolService
import br.all.protocol.controller.ProtocolController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulUpdateProtocolPresenter: UpdateProtocolPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: UpdateProtocolService.ResponseModel) {
        val (researcher, systematicStudy) = response
        val viewModel = ViewModel(researcher, systematicStudy)

        val link = linkTo<ProtocolController> {
            findById(systematicStudy)
        }.withSelfRel()
        viewModel.add(link)

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
    ): RepresentationModel<ViewModel>()
}
