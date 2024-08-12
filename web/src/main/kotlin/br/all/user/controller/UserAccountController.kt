package br.all.user.controller

import br.all.application.user.CredentialsService
import br.all.application.user.create.RegisterUserAccountService
import br.all.application.user.create.RegisterUserAccountService.RequestModel
import br.all.user.presenter.RestfullRegisterUserAccountPresenter
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
    @Operation(summary = "Create a new user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Success creating a user",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CredentialsService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail creating a user - invalid input",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Fail creating a user - user already exists",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun registerUser(@RequestBody request: RequestModel): ResponseEntity<*> {
        val presenter = RestfullRegisterUserAccountPresenter()
        val encodedPasswordRequest = request.copy(password = encoder.encode(request.password))
        registerUserAccountService.register(presenter, encodedPasswordRequest)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}