package br.all.review.presenter

import br.all.application.review.find.presenter.FindSystematicStudyParticipantsPresenter
import br.all.application.review.find.services.FindSystematicStudyParticipantsService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulFindSystematicStudyParticipantsPresenter :
    FindSystematicStudyParticipantsPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {

        val restfulResponse = ViewModel(
            response.systematicStudyId,
            response.participants.size,
            response.participants
        )

        responseEntity = ResponseEntity
            .status(HttpStatus.OK)
            .body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val systematicStudyId: UUID,
        val size: Int,
        val participants: Set<UUID>,
    ) : RepresentationModel<ViewModel>()
}