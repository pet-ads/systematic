package br.all.collaborator.presenter

import br.all.application.collaborator.find.FindAllCollaboratorsPresenter
import br.all.application.collaborator.find.FindAllCollaboratorsService.InvitedCollaboratorDto
import br.all.application.collaborator.find.FindAllCollaboratorsService.CollaboratorOnReviewDto
import br.all.application.collaborator.find.FindAllCollaboratorsService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status

class RestfulFindAllCollaboratorsPresenter : FindAllCollaboratorsPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val viewModel = ViewModel(
            response.invited,
            response.collaborators,
        )

        responseEntity = status(HttpStatus.OK).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val invited: List<InvitedCollaboratorDto>,
        val collaborators: List<CollaboratorOnReviewDto>,
    ): RepresentationModel<ViewModel>()
}