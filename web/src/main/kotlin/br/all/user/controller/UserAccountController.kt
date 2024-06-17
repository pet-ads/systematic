package br.all.user.controller

import br.all.application.user.create.RegisterUserAccountService
import br.all.application.user.create.RegisterUserAccountService.RequestModel
import br.all.user.presenter.RestfullRegisterUserAccountPresenter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/user")
class UserAccountController(
    private val registerUserAccountService: RegisterUserAccountService,
    private val encoder: PasswordEncoder
) {

    @PostMapping
    fun registerUser(@RequestBody request: RequestModel): ResponseEntity<*> {
        val presenter = RestfullRegisterUserAccountPresenter()
        val encodedPasswordRequest = request.copy(password = encoder.encode(request.password))
        registerUserAccountService.register(presenter, encodedPasswordRequest)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}