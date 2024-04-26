package br.all.application.user.find

import br.all.application.user.find.LoadCredentialsByTokenService.ResponseModel
import br.all.application.user.repository.UserAccountRepository

class LoadCredentialsByTokenServiceImpl(private val repository: UserAccountRepository) : LoadCredentialsByTokenService {

    override fun loadByToken(refreshToken: String): ResponseModel {
        val userDto = repository.loadCredentialsByToken(refreshToken)
            ?: throw NoSuchElementException("Token not found: $refreshToken")

        return ResponseModel(userDto.id, userDto.username)
    }

}