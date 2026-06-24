package br.all.infrastructure.user

import br.all.application.user.repository.AccountCredentialsDto
import br.all.application.user.repository.UserAccountDto
import br.all.application.user.repository.UserProfileDto
import br.all.application.user.repository.UserSummaryDto

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
    val account =  UserAccountEntity(id, name, credentials, email, country, affiliation, createdAt)

    credentials.userAccount = account

    return account
}

fun AccountCredentialsEntity.toAccountCredentialsDto() = AccountCredentialsDto(id, username, password, authorities, refreshToken, isEnabled)

fun UserAccountEntity.toUserProfileDto() = UserProfileDto(
    id = this.id,
    name = this.name,
    email = this.email,
    country = this.country,
    affiliation = this.affiliation,
    isEnabled = this.accountCredentialsEntity.isEnabled,
)

fun UserAccountEntity.toUserSummaryDto() = UserSummaryDto(
    id = this.id,
    username = this.accountCredentialsEntity.username,
    email = this.email,
)