package br.all

import br.all.utils.CreateSystematicReviewUseCase
import br.all.utils.RegisterUserUseCase
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.password.PasswordEncoder


@SpringBootApplication
class WebApplication{
    //TODO fix the token get claims returning 500 for invalid token format

    @Bean
    fun run(
        encoder: PasswordEncoder,
        register: RegisterUserUseCase,
        create: CreateSystematicReviewUseCase,
        ) = CommandLineRunner {
        val password = encoder.encode("admin")

        val lucasUserAccount = register.registerUserAccount("buenolro", password)

        create.createReview(lucasUserAccount.id.value(), setOf(lucasUserAccount.id.value()))
    }
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}
