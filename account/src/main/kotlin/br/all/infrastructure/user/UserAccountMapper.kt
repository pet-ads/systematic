package br.all.infrastructure.user

import br.all.application.user.repository.AccountCredentialsDto
import br.all.application.user.repository.UserAccountDto
import br.all.application.user.repository.UserProfileDto

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
    return UserAccountEntity(id, name, credentials, email, country, affiliation, createdAt)
}

fun AccountCredentialsEntity.toAccountCredentialsDto() = AccountCredentialsDto(id, username, password, authorities, refreshToken)

fun UserAccountEntity.toUserProfileDto() = UserProfileDto(
    id = this.id,
    name = this.name,
    email = this.email,
    country = this.country,
    affiliation = this.affiliation
)