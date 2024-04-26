package br.all.domain.user

import java.time.LocalDateTime

class UserAccount(
    val id: UserAccountId,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var email: Email,
    var country: Text,
    var affiliation: String,
    username: Username,
    password: String,
){
    private var _accountCredentials = AccountCredentials(username, password, null)
    val accountCredentials get() =_accountCredentials

    fun changeUsername(newUsername: Username) {
        _accountCredentials = AccountCredentials(newUsername, _accountCredentials.password, null)
    }

    fun changePassword(password: String) {
        _accountCredentials = AccountCredentials(_accountCredentials.username, password, null)
    }
}