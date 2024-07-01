package br.all.security.service

import br.all.application.user.find.LoadAccountCredentialsService
import br.all.application.user.update.UpdateRefreshTokenService
import br.all.application.user.update.UpdateRefreshTokenService.RequestModel
import br.all.review.controller.SystematicStudyController
import br.all.review.requests.PostRequest
import br.all.security.auth.AuthenticationRequest
import br.all.security.auth.AuthenticationResponse
import br.all.security.config.JwtProperties
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
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
    fun authenticate(request: AuthenticationRequest): AuthenticationResponseModel {
        authManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password))
        val user = userDetailService.loadUserByUsername(request.username) as ApplicationUser

        val token = generateToken(user, jwtProperties.accessTokenExpiration)
        val refreshToken = generateToken(user, jwtProperties.refreshTokenExpiration)

        val  updateTokenRequest = RequestModel(user.id, refreshToken)
        updateRefreshTokenService.update(updateTokenRequest)

        val responseModel = AuthenticationResponseModel(token, refreshToken)
        val ownerStudies = linkToFindAllByOwner(user.id)
        val createSystematicStudy = linkToPostSystematicStudy()

        responseModel.add(ownerStudies, createSystematicStudy)

        return responseModel
    }

    private fun linkToFindAllByOwner(ownerId: UUID) = linkTo<SystematicStudyController> {
        findAllSystematicStudiesByOwner(ownerId)
    }.withRel("find-my-reviews")

    private fun linkToPostSystematicStudy() = linkTo<SystematicStudyController> {
        postSystematicStudy(PostRequest("title", "description", setOf(UUID.randomUUID())))
    }.withRel("create-review")

    private fun generateToken(user: ApplicationUser, duration: Long) = tokenService.generateToken(
        user,
        Date(System.currentTimeMillis() + duration),
        mapOf("id" to user.id)
    )

    fun refreshAccessToken(refreshToken: String): String? {
        val username = tokenService.extractUsername(refreshToken) ?: return null

        val currentUserDetails = userDetailService.loadUserByUsername(username) as ApplicationUser
        val tokenUserCredentials = loadCredentialsService.loadSimpleCredentialsByToken(refreshToken)

        if(canNotRefreshAccessToken(refreshToken, currentUserDetails.id, tokenUserCredentials.id)) return null
        return generateToken(currentUserDetails, jwtProperties.accessTokenExpiration)
    }

    private fun canNotRefreshAccessToken(
        refreshToken: String,
        currentUserId: UUID,
        refreshTokenUserId: UUID?
    ) = tokenService.isExpired(refreshToken) || currentUserId != refreshTokenUserId

    data class AuthenticationResponseModel(
        val accessToken: String,
        val refreshToken: String
    ): RepresentationModel<AuthenticationResponseModel>()
}