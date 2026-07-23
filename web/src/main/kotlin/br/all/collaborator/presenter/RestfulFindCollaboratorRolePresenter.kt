package br.all.collaborator.presenter

import br.all.application.collaborator.find.FindCollaboratorRoleService.ResponseModel
import br.all.application.collaborator.find.FindCollaboratorRolePresenter
import br.all.domain.shared.user.Role
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status

class RestfulFindCollaboratorRolePresenter : FindCollaboratorRolePresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val viewModel = ViewModel(
            response.role,
        )

        responseEntity = status(HttpStatus.OK).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val role: Role,
    ): RepresentationModel<ViewModel>()
}