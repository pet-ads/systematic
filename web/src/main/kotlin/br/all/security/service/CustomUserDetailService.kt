package br.all.security.service

import br.all.application.user.find.LoadAccountCredentialsService
import br.all.application.user.find.LoadAccountCredentialsService.ResponseModel
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

class CustomUserDetailService(private val service: LoadAccountCredentialsService) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        service.loadByUsername(username).toUserDetails()
}

private fun ResponseModel.toUserDetails() =
    ApplicationUser(this.id, this.username, this.password)
