package br.all.security.auth

import br.all.security.service.AuthenticationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(private val authenticationService: AuthenticationService) {

    @PostMapping
    @Operation(summary = "Performs a authentication operation")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Success authentication",
            content = [Content(schema = Schema(hidden = true))]),
        ApiResponse(
            responseCode = "400",
            description = "Fail authentication - invalid input",
            content = [Content(schema = Schema(hidden = true))]),
        ApiResponse(
            responseCode = "401",
            description = "Fail authentication - invalid credentials",
            content = [Content(schema = Schema(hidden = true))]),
        ApiResponse(
            responseCode = "404",
            description = "Fail authentication - user not found",
            content = [Content(schema = Schema(hidden = true))]),
    ])
    fun authenticate(
        @RequestBody authRequest: AuthenticationRequest,
        response: HttpServletResponse
    ): AuthenticationService.AuthenticationResponseModel =
        authenticationService.authenticate(authRequest, response)

    @PostMapping("/refresh")
    @Operation(summary = "Performs a refresh token operation")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Success refresh"),
        ApiResponse(
            responseCode = "400",
            description = "Fail refresh - refresh token not found",
            content = [Content(schema = Schema(hidden = true))]),
        ApiResponse(
            responseCode = "401",
            description = "Fail refresh - expired refresh token",
            content = [Content(schema = Schema(hidden = true))]),
        ApiResponse(
            responseCode = "403",
            description = "Fail refresh - invalid refresh token",
            content = [Content(schema = Schema(hidden = true))]),
    ])
    fun refreshToken(request: HttpServletRequest,
                     response: HttpServletResponse,){
        authenticationService.refreshAccessToken(request, response)
            ?.mapToTokenResponse()
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token")
    }
    private fun String.mapToTokenResponse() = TokenResponse(this)
}