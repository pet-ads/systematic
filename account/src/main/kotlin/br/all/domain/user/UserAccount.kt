package br.all.domain.user

import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.user.Email
import br.all.domain.shared.user.Text
import java.time.LocalDateTime
import java.util.UUID

class UserAccount(
    id: UserAccountId,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var email: Email,
    var country: Text,
    var affiliation: String,
    username: Username,
    password: String,
    authorities: Set<Authority>,
) : Entity<UUID>(id) {
    private var _accountCredentials = AccountCredentials(username, password, authorities)
    val accountCredentials get() =_accountCredentials

    fun changeUsername(newUsername: Username) {
        _accountCredentials = AccountCredentials(
            newUsername,
            _accountCredentials.password,
            _accountCredentials.authorities)
    }

    fun changePassword(password: String) {
        _accountCredentials = AccountCredentials(
            _accountCredentials.username,
            password,
            _accountCredentials.authorities)
    }
}