package br.all.user.controller

import br.all.application.user.CredentialsService
import br.all.application.user.create.RegisterUserAccountService
import br.all.application.user.create.RegisterUserAccountService.RequestModel
import br.all.application.user.find.RetrieveUserProfileService
import br.all.application.user.update.ChangeAccountPasswordService
import br.all.application.user.update.PatchUserProfileService
import br.all.security.service.AuthenticationInfoService
import br.all.security.service.AuthenticationService
import br.all.user.presenter.RestfulChangeAccountPasswordPresenter
import br.all.user.presenter.RestfulPatchUserProfilePresenter
import br.all.user.presenter.RestfulRegisterUserAccountPresenter
import br.all.user.presenter.RestfulRetrieveUserProfilePresenter
import br.all.user.requests.ChangeAccountPasswordRequest
import br.all.user.requests.PatchUserProfileRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/user")
class UserAccountController(
    private val registerUserAccountService: RegisterUserAccountService,
    private val encoder: PasswordEncoder,
    private val retrieveUserProfileService: RetrieveUserProfileService,
    private val authenticationInfoService: AuthenticationInfoService,
    private val patchUserProfileService: PatchUserProfileService,
    private val changeAccountPasswordService: ChangeAccountPasswordService,
    private val authenticationService: AuthenticationService
) {

    @PostMapping
    @Operation(summary = "Create an new user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Success creating an user",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CredentialsService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Fail creating an user - invalid input",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Fail creating an user - user already exists",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ]
    )
    fun registerUser(@RequestBody request: RequestModel): ResponseEntity<*> {
        val presenter = RestfulRegisterUserAccountPresenter()
        val encodedPasswordRequest = request.copy(password = encoder.encode(request.password))
        registerUserAccountService.register(presenter, encodedPasswordRequest)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/profile")
    @Operation(summary = "Retrieve public information of an user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success retrieving user profile",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RetrieveUserProfileService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail retrieving user profile - unauthenticated collaborator",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail retrieving user profile - unauthorized collaborator",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail retrieving user profile - nonexistent user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ])
    fun retrieveUserPublicData(): ResponseEntity<*> {
        val presenter = RestfulRetrieveUserProfilePresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = RetrieveUserProfileService.RequestModel(userId)

        retrieveUserProfileService.retrieveData(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }


    @PatchMapping("/profile")
    @Operation(summary = "Update public information of an user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success updating user profile",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = PatchUserProfileService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail updating user profile - unauthenticated collaborator",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail updating user profile - unauthorized collaborator",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail updating user profile - nonexistent user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ])
    fun patchUserPublicData(@RequestBody body: PatchUserProfileRequest): ResponseEntity<*> {
        val presenter = RestfulPatchUserProfilePresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val request = PatchUserProfileService.RequestModel(
            userId = userId,
            name = body.name,
            email = body.email,
            affiliation = body.affiliation,
            country = body.country
        )

        patchUserProfileService.patchProfile(presenter, request)
        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping("/change-password")
    @Operation(summary = "Update password of an user account and logout")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success updating account password",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ChangeAccountPasswordService.ResponseModel::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Fail updating account password - unauthenticated collaborator",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Fail updating account password - unauthorized collaborator",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Fail updating account password - nonexistent user",
                content = [Content(schema = Schema(hidden = true))]
            ),
        ])
    fun putAccountPassword(
        @RequestBody body: ChangeAccountPasswordRequest,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val presenter = RestfulChangeAccountPasswordPresenter()
        val userId = authenticationInfoService.getAuthenticatedUserId()
        val changePasswordRequest = ChangeAccountPasswordService.RequestModel(
            userId = userId,
            oldPassword = body.oldPassword,
            newPassword = body.newPassword,
            confirmPassword = body.confirmPassword
        )

        changeAccountPasswordService.changePassword(presenter, changePasswordRequest)

        if (presenter.responseEntity?.statusCode?.isError == true) {
            return ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
        }

        authenticationService.logout(request, response)

        return presenter.responseEntity ?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}