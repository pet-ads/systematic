package br.all.security.config

import br.all.application.user.create.RegisterUserAccountService
import br.all.application.user.create.RegisterUserAccountService.RequestModel
import br.all.user.presenter.RestfullRegisterUserAccountPresenter
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthTestConfig(
    private val registerUserAccountService: RegisterUserAccountService,
    private val encoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val username = "admin"
        val email = "admin@admin.com"
        val password = "admin"
        val country = "Brazil"
        val affiliation = "IFSP"

        val request = RequestModel(
            username = username,
            email = email,
            password = encoder.encode(password),
            country = country,
            affiliation = affiliation
        )

        val presenter = RestfullRegisterUserAccountPresenter()

        try {
            registerUserAccountService.register(presenter, request)
            println("✅ Usuário de teste criado: $username / $password")
        } catch (ex: Exception) {
            println("🔹 Usuário de teste já existe ou ocorreu um erro: ${ex.message}")
        }
    }
}
