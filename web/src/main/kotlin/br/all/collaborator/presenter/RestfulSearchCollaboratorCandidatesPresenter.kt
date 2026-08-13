package br.all.collaborator.presenter

import br.all.application.collaborator.find.SearchCollaboratorCandidatesPresenter
import br.all.application.collaborator.find.SearchCollaboratorCandidatesService.SearchResponseModel
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class RestfulSearchCollaboratorCandidatesPresenter: SearchCollaboratorCandidatesPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: SearchResponseModel) {
        val restfulResponse = ViewModel(response)

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researchers: SearchResponseModel,
    ): RepresentationModel<ViewModel>()
}
