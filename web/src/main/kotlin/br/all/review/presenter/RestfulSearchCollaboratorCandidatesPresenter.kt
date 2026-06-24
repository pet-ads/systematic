package br.all.review.presenter

import br.all.application.review.find.presenter.SearchCollaboratorCandidatesPresenter
import br.all.application.review.find.services.SearchCollaboratorCandidatesService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class RestfulSearchCollaboratorCandidatesPresenter: SearchCollaboratorCandidatesPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response)

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researchers: ResponseModel,
    ): RepresentationModel<ViewModel>()
}
