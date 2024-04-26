package br.all.application.user.find

import java.util.UUID

interface LoadAccountCredentialsService {

    data class UserAuthenticationCredentials(
        val id: UUID,
        val username: String,
        val password: String
    )

    data class UserSimpleCredentials(
        val id: UUID,
        val username: String,
    )

    fun loadAuthenticationCredentialsByUsername(username: String): UserAuthenticationCredentials

    fun loadSimpleCredentialsByToken(refreshToken: String): UserSimpleCredentials

    fun loadSimpleCredentialsById(id: UUID): UserSimpleCredentials

}