package br.all.security.config

import br.all.security.service.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService
) : OncePerRequestFilter() {

    private val matchersToSkip: List<RequestMatcher> = listOf(
        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/v1/user"),
        PathPatternRequestMatcher.withDefaults().matcher("/api/v1/auth/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/api/v1/auth/logout/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/webjars/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/error"),
        PathPatternRequestMatcher.withDefaults().matcher("/swagger-ui.html"),
        PathPatternRequestMatcher.withDefaults().matcher("/swagger-ui/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/swagger-resources/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/v3/api-docs/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/configuration/ui"),
        PathPatternRequestMatcher.withDefaults().matcher("/configuration/security"),
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        if(matchersToSkip.any { it.matches(request) }){
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("Authorization")
        if (authHeader.doesNotContainBearerToken()) {
            filterChain.doFilter(request, response)
            return
        }

        val jwtToken = authHeader!!.extractBearerToken()

        if(!isValidJwtFormat(jwtToken) || tokenService.isExpired(jwtToken)){
            filterChain.doFilter(request, response)
            return
        }

        val username = tokenService.extractUsername(jwtToken)

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val foundUser = userDetailsService.loadUserByUsername(username)
            if (tokenService.isValid(jwtToken, foundUser)) {
                updateContext(foundUser, request)
            }
            filterChain.doFilter(request, response)
        }
    }

    private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }

    private fun String?.doesNotContainBearerToken() = this == null || !this.startsWith("Bearer ")

    private fun String.extractBearerToken(): String = this.substringAfter("Bearer ")

    fun isValidJwtFormat(token: String): Boolean {
        val jwtPattern = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$"
        return token.matches(jwtPattern.toRegex())
    }
}

