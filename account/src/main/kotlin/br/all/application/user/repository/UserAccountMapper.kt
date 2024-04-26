package br.all.application.user.repository

import br.all.domain.user.UserAccount

fun UserAccount.toDto() = UserAccountDto(
    id.value,
    accountCredentials.username.value,
    accountCredentials.password,
    email.value,
    country.value,
    affiliation,
    createdAt,
    null,
    accountCredentials.isAccountNonExpired,
    accountCredentials.isAccountNonLocked,
    accountCredentials.isCredentialsNonExpired,
    accountCredentials.isEnabled
)

