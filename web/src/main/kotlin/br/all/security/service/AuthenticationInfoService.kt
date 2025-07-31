package br.all.security.service

import br.all.domain.shared.exception.UnauthorizedUserException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationInfoService {
    fun getAuthenticatedUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated)
            throw UnauthorizedUserException("Unauthorized user request.")
        val applicationUser = authentication.principal as ApplicationUser
        return applicationUser.id
    }
}