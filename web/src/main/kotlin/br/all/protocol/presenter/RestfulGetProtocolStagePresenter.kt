package br.all.protocol.presenter

import br.all.application.protocol.find.GetProtocolStagePresenter
import br.all.application.protocol.find.GetProtocolStageService.ResponseModel
import br.all.application.protocol.find.GetProtocolStageService.ProtocolStage
import br.all.shared.error.createErrorResponseFrom
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID

class RestfulGetProtocolStagePresenter(
    private val linksFactory: LinksFactory
) : GetProtocolStagePresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val viewModel = ViewModel(response.userId, response.systematicStudyId, response.currentStage)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val currentStage: ProtocolStage,
    ) : RepresentationModel<ViewModel>()
}