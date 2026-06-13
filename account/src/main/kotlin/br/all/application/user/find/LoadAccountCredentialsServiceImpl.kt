package br.all.application.user.find

import br.all.application.user.find.LoadAccountCredentialsService.UserAuthenticationCredentials
import br.all.application.user.find.LoadAccountCredentialsService.UserSimpleCredentials
import br.all.application.user.find.LoadAccountCredentialsService.UserEnabledCredentials
import br.all.application.user.find.LoadAccountCredentialsService.UserInformation
import br.all.application.user.repository.UserAccountRepository
import java.util.*
import kotlin.NoSuchElementException

class LoadAccountCredentialsServiceImpl(private val repository: UserAccountRepository) : LoadAccountCredentialsService {

    override fun loadAuthenticationCredentialsByUsername(username: String): UserAuthenticationCredentials {
        val userDto = repository.loadCredentialsByUsername(username)
            ?: throw NoSuchElementException("Username $username not found.")

        return UserAuthenticationCredentials(userDto.id, userDto.username, userDto.password, userDto.authorities)
    }

    override fun loadSimpleCredentialsByToken(refreshToken: String): UserSimpleCredentials {
        val userDto = repository.loadCredentialsByToken(refreshToken)
            ?: throw NoSuchElementException("Token not found: $refreshToken")

        return UserSimpleCredentials(userDto.id, userDto.username, userDto.authorities)
    }

    override fun loadSimpleCredentialsById(id: UUID): UserSimpleCredentials {
        val userDto = repository.loadCredentialsById(id)
            ?: throw NoSuchElementException("User id $id not found.")

        return UserSimpleCredentials(userDto.id, userDto.username, userDto.authorities)
    }

    override fun loadEnabledCredentialsById(id: UUID): UserEnabledCredentials {
        val userDto = repository.loadCredentialsById(id)
            ?: throw NoSuchElementException("User id $id not found.")

        return UserEnabledCredentials(userDto.id, userDto.username, userDto.authorities, userDto.isEnabled)
    }

    override fun loadUserInformationByUsername(username: String): UserInformation {
        val userDto = repository.loadUserProfileByUsername(username)
            ?: throw NoSuchElementException("Username $username not found.")

        return UserInformation(userDto.id, userDto.name, userDto.country, userDto.isEnabled, userDto.email)
    }

    override fun loadUserInformationById(id: UUID): UserInformation {
        val userDto = repository.loadFullUserAccountById(id)
            ?: throw NoSuchElementException("User not found.")

        return UserInformation(userDto.id, userDto.username, userDto.country, userDto.isEnabled, userDto.email)
    }

}