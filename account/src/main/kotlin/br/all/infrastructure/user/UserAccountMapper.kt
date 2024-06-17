package br.all.infrastructure.user

import br.all.application.user.repository.AccountCredentialsDto
import br.all.application.user.repository.UserAccountDto

fun UserAccountDto.toUserAccountEntity(): UserAccountEntity {
    val credentials = AccountCredentialsEntity(
        id,
        username,
        password,
        authorities,
        refreshToken,
        isAccountNonExpired,
        isAccountNonExpired,
        isCredentialsNonExpired,
        isEnabled
    )
    return UserAccountEntity(id, credentials, email, country, affiliation, createdAt)
}

fun AccountCredentialsEntity.toAccountCredentialsDto() = AccountCredentialsDto(id, username, password, authorities, refreshToken)