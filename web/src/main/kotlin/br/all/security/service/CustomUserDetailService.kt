package br.all.security.service

import br.all.application.user.find.LoadAccountCredentialsService
import br.all.application.user.find.LoadAccountCredentialsService.UserAuthenticationCredentials
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

class CustomUserDetailService(private val service: LoadAccountCredentialsService) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        service.loadAuthenticationCredentialsByUsername(username).toUserDetails()
}

private fun UserAuthenticationCredentials.toUserDetails() =
    ApplicationUser(this.id, this.username, this.password)
