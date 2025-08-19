package br.all.domain.user

import br.all.domain.shared.user.Username

class AccountCredentials (
    val username: Username,
    val password: String,
    val authorities: Set<Authority>,
    val refreshToken: String? = null,
    val isAccountNonExpired : Boolean = true,
    val isAccountNonLocked : Boolean = true,
    val isCredentialsNonExpired : Boolean = true,
    val isEnabled : Boolean = true,
)