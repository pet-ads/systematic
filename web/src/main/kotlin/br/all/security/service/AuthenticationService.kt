package br.all.security.service

import br.all.application.user.find.LoadAccountCredentialsService
import br.all.application.user.update.UpdateRefreshTokenService
import br.all.application.user.update.UpdateRefreshTokenService.RequestModel
import br.all.security.auth.AuthenticationRequest
import br.all.security.config.JwtProperties
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import br.all.utils.LinksFactory
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailService: UserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val loadCredentialsService: LoadAccountCredentialsService,
    private val updateRefreshTokenService: UpdateRefreshTokenService,
    private val linksFactory: LinksFactory
) {

    private val refreshCookieExpiration = jwtProperties.refreshTokenExpiration / 1000

    fun authenticate(request: AuthenticationRequest, response: HttpServletResponse): AuthenticationResponseModel {
        authManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password))
        val user = userDetailService.loadUserByUsername(request.username) as ApplicationUser

        val token = generateToken(user, jwtProperties.accessTokenExpiration)
        val refreshToken = generateToken(user, jwtProperties.refreshTokenExpiration)

        val  updateTokenRequest = RequestModel(user.id, refreshToken)
        updateRefreshTokenService.update(updateTokenRequest)


        val refreshCookie = generateCookieFromToken("refreshToken", refreshToken, refreshCookieExpiration)

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())

        val responseModel = AuthenticationResponseModel(token)

        val ownerStudies = linksFactory.findMyReviews(user.id)
        val createSystematicStudy = linksFactory.createReview()

        responseModel.add(ownerStudies, createSystematicStudy)

        return responseModel
    }

    private fun generateToken(user: ApplicationUser, duration: Long) = tokenService.generateToken(
        user,
        Date(System.currentTimeMillis() + duration),
        mapOf("id" to user.id)
    )

    fun refreshAccessToken(request: HttpServletRequest,
                           response: HttpServletResponse): String? {
        if(request.cookies.isNullOrEmpty()) return null

        val refreshToken = request.cookies.lastOrNull { cookie -> cookie.name.equals("refreshToken") }?.value
        if(refreshToken == null) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token not found")

        val username = tokenService.extractUsername(refreshToken) ?: return null

        val currentUserDetails = userDetailService.loadUserByUsername(username) as ApplicationUser
        val tokenUserCredentials = loadCredentialsService.loadSimpleCredentialsByToken(refreshToken)

        if(tokenService.isExpired(refreshToken))
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Expired refresh token")

        if(currentUserDetails.id != tokenUserCredentials.id) return null

        val accessToken = generateToken(currentUserDetails, jwtProperties.accessTokenExpiration)

        return accessToken
    }

    fun logout(request: HttpServletRequest, response: HttpServletResponse) {
        if(request.cookies.isNullOrEmpty()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "No login found")

        val refreshCookie = generateCookieFromToken("refreshToken", null.toString(), 0)

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
    }

    private fun generateCookieFromToken(
        name: String,
        token: String,
        maxAge: Long
    ) = ResponseCookie.from(name, token)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(maxAge)
            .build()

    inner class AuthenticationResponseModel(val accessToken: String): RepresentationModel<AuthenticationResponseModel>()
}