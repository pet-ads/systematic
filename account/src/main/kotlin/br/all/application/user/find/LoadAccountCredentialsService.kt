package br.all.application.user.find

import java.util.UUID

interface LoadAccountCredentialsService {

    data class UserAuthenticationCredentials(
        val id: UUID,
        val username: String,
        val password: String,
        val authorities: Set<String>
    )

    data class UserSimpleCredentials(
        val id: UUID,
        val username: String,
        val authorities: Set<String>
    )

    data class UserEnabledCredentials(
        val id: UUID,
        val username: String,
        val authorities: Set<String>,
        val isEnabled : Boolean
    )

    data class UserInformation(
        val id: UUID,
        val username: String,
        val country: String,
        val isEnabled : Boolean,
        val email: String,
    )

    fun loadAuthenticationCredentialsByUsername(username: String): UserAuthenticationCredentials

    fun loadSimpleCredentialsByToken(refreshToken: String): UserSimpleCredentials

    fun loadSimpleCredentialsById(id: UUID): UserSimpleCredentials

    fun loadEnabledCredentialsById(id: UUID): UserEnabledCredentials

    fun loadUserInformationByUsername(username: String): UserInformation

    fun loadUserInformationById(id: UUID): UserInformation

}