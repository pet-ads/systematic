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

fun UserAccountEntity.toUserAccountDto() = UserAccountDto(
    id = this.id,
    username = this.accountCredentialsEntity.username,
    password = this.accountCredentialsEntity.password,
    email = this.email,
    country = this.country,
    affiliation = this.affiliation,
    createdAt = this.createdAt,
    authorities = this.accountCredentialsEntity.authorities,
    refreshToken = this.accountCredentialsEntity.refreshToken,
    isAccountNonExpired = this.accountCredentialsEntity.isAccountNonExpired,
    isAccountNonLocked = this.accountCredentialsEntity.isAccountNonLocked,
    isCredentialsNonExpired = this.accountCredentialsEntity.isCredentialsNonExpired,
    isEnabled = this.accountCredentialsEntity.isEnabled
)