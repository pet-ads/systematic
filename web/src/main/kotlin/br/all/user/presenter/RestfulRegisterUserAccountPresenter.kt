package br.all.user.presenter

import br.all.application.user.create.RegisterUserAccountPresenter
import br.all.application.user.create.RegisterUserAccountService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import java.util.*

class RestfulRegisterUserAccountPresenter : RegisterUserAccountPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.id, response.username, response.email)
        responseEntity = status(HttpStatus.CREATED).body(restfulResponse)

    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    private data class ViewModel(
        val id: UUID,
        val username: String,
        val email: String
    ) : RepresentationModel<ViewModel>()

}