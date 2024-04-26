package br.all.application.user.find

import br.all.application.user.find.LoadAccountCredentialsService.ResponseModel
import br.all.application.user.repository.UserAccountRepository

class LoadAccountCredentialsServiceImpl(private val repository: UserAccountRepository) : LoadAccountCredentialsService {

    override fun loadByUsername(username: String): ResponseModel {
        val userDto = repository.loadCredentialsByUsername(username)
            ?: throw NoSuchElementException("Username $username not found.")

        return ResponseModel(userDto.id, userDto.username, userDto.password)
    }

}