package br.all.security.config

import br.all.security.service.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var jwtToken: String? = null

        if(request.cookies != null){
            for(cookie in request.cookies){
                if(cookie.name.equals("accessToken")){
                    jwtToken = cookie.value
                }
            }
        }

        if(jwtToken == null){
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

}

