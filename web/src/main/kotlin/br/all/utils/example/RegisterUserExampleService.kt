package br.all.utils.example

import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.repository.toDto
import br.all.domain.shared.user.Email
import br.all.domain.shared.user.Name
import br.all.domain.shared.user.Text
import br.all.domain.shared.user.Username
import br.all.domain.user.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class RegisterUserExampleService (
    private val repo: UserAccountRepository,
    ){
    fun registerUserAccount(
        username: String,
        password: String,
        email: String = "lucas@gmail.com",
        country: String = "Brazil",
        affiliation: String = "IFSP",
        name: String = "Lucas"
    ): UserAccount {

        val newUserAccount = UserAccount(
            id = UserAccountId(UUID.randomUUID()),
            username = Username(username),
            password = password,
            email = Email(email),
            country = Text(country),
            affiliation = affiliation,
            authorities = setOf(Authority.USER),
            name = Name(name),
        )

        repo.save(newUserAccount.toDto())
        return newUserAccount
    }
}