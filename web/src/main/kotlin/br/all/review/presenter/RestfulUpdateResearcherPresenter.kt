package br.all.review.presenter

import br.all.application.review.update.presenter.UpdateResearcherRolePresenter
import br.all.application.review.update.services.UpdateResearcherRoleService.ResponseModel
import br.all.domain.shared.user.Role
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulUpdateResearcherPresenter: UpdateResearcherRolePresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.researcherId, response.role)

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val researcherId: UUID,
        val role: Role,
    ): RepresentationModel<ViewModel>()
}
