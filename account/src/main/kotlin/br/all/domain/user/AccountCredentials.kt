package br.all.domain.user

class AccountCredentials (
    val username: Username,
    val password: String,
    val refreshToken: String?,
    val isAccountNonExpired : Boolean = true,
    val isAccountNonLocked : Boolean = true,
    val isCredentialsNonExpired : Boolean = true,
    val isEnabled : Boolean = true,
)