package br.all.security.service

import br.all.application.user.find.LoadAccountCredentialsService
import br.all.application.user.update.UpdateRefreshTokenService
import br.all.application.user.update.UpdateRefreshTokenService.RequestModel
import br.all.review.controller.SystematicStudyController
import br.all.review.requests.PostRequest
import br.all.security.auth.AuthenticationRequest
import br.all.security.config.JwtProperties
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailService: UserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val loadCredentialsService: LoadAccountCredentialsService,
    private val updateRefreshTokenService: UpdateRefreshTokenService
) {
    fun authenticate(request: AuthenticationRequest, response: HttpServletResponse): AuthenticationResponseModel {
        authManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password))
        val user = userDetailService.loadUserByUsername(request.username) as ApplicationUser

        val token = generateToken(user, jwtProperties.accessTokenExpiration)
        val refreshToken = generateToken(user, jwtProperties.refreshTokenExpiration)

        val  updateTokenRequest = RequestModel(user.id, refreshToken)
        updateRefreshTokenService.update(updateTokenRequest)

        val cookie = ResponseCookie.from("accessToken", token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(jwtProperties.accessTokenExpiration)
            .build()

        val refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/v1/auth/refresh")
            .maxAge(jwtProperties.refreshTokenExpiration)
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())

        val responseModel = AuthenticationResponseModel()
        val ownerStudies = linkToFindAllByOwner(user.id)
        val createSystematicStudy = linkToPostSystematicStudy()

        responseModel.add(ownerStudies, createSystematicStudy)

        return responseModel
    }

    private fun linkToFindAllByOwner(ownerId: UUID) = linkTo<SystematicStudyController> {
        findAllSystematicStudiesByOwner(ownerId)
    }.withRel("find-my-reviews")

    private fun linkToPostSystematicStudy() = linkTo<SystematicStudyController> {
        postSystematicStudy(PostRequest("title", "description", setOf()))
    }.withRel("create-review")

    private fun generateToken(user: ApplicationUser, duration: Long) = tokenService.generateToken(
        user,
        Date(System.currentTimeMillis() + duration),
        mapOf("id" to user.id)
    )

    fun refreshAccessToken(request: HttpServletRequest,
                           response: HttpServletResponse): String? {
        if(request.cookies.isNullOrEmpty()) return null

        val refreshToken = request.cookies.firstOrNull { cookie -> cookie.name.equals("refreshToken") }?.value
        if(refreshToken == null) return null

        val username = tokenService.extractUsername(refreshToken) ?: return null

        val currentUserDetails = userDetailService.loadUserByUsername(username) as ApplicationUser
        val tokenUserCredentials = loadCredentialsService.loadSimpleCredentialsByToken(refreshToken)

        if(canNotRefreshAccessToken(refreshToken, currentUserDetails.id, tokenUserCredentials.id)) return null

        val accessToken = generateToken(currentUserDetails, jwtProperties.accessTokenExpiration)

        val cookie = ResponseCookie.from("accessToken", accessToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(jwtProperties.accessTokenExpiration)
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())

        return accessToken
    }

    private fun canNotRefreshAccessToken(
        refreshToken: String,
        currentUserId: UUID,
        refreshTokenUserId: UUID?
    ) = tokenService.isExpired(refreshToken) || currentUserId != refreshTokenUserId

    inner class AuthenticationResponseModel: RepresentationModel<AuthenticationResponseModel>()
}