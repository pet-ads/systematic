package br.all.application.user.find

import br.all.application.user.find.LoadAccountCredentialsService.UserAuthenticationCredentials
import br.all.application.user.find.LoadAccountCredentialsService.UserSimpleCredentials
import br.all.application.user.repository.UserAccountRepository
import java.util.*
import kotlin.NoSuchElementException

class LoadAccountCredentialsServiceImpl(private val repository: UserAccountRepository) : LoadAccountCredentialsService {

    override fun loadAuthenticationCredentialsByUsername(username: String): UserAuthenticationCredentials {
        val userDto = repository.loadCredentialsByUsername(username)
            ?: throw NoSuchElementException("Username $username not found.")

        return UserAuthenticationCredentials(userDto.id, userDto.username, userDto.password)
    }

    override fun loadSimpleCredentialsByToken(refreshToken: String): UserSimpleCredentials {
        val userDto = repository.loadCredentialsByToken(refreshToken)
            ?: throw NoSuchElementException("Token not found: $refreshToken")

        return UserSimpleCredentials(userDto.id, userDto.username)
    }

    override fun loadSimpleCredentialsById(id: UUID): UserSimpleCredentials {
        val userDto = repository.loadCredentialsById(id)
            ?: throw NoSuchElementException("Username id $id not found.")

        return UserSimpleCredentials(userDto.id, userDto.username)
    }

}