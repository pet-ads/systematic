package br.all.utils.example

import br.all.application.user.repository.UserAccountRepository
import org.springframework.stereotype.Service

@Service
class VerifyIfExistsUserExampleService (
    private val repo: UserAccountRepository,
    ){
    fun existsUser(): Boolean {
        return repo.existsByEmail("lucas@gmail.com")
    }
}