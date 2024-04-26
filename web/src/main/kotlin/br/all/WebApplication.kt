package br.all

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import br.all.infrastructure.user.JpaAccountCredentialsRepository
import br.all.infrastructure.user.UserAccountEntity
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*


@SpringBootApplication
class WebApplication{

    //TODO Check the integration between account and review modules
    //TODO See how to remove researcher info from every endpoint
    //TODO Check how to fix API tests
    //TODO write test cases route for PET members for account
    //TODO write test case guide for checking for 401
    //TODO fix the token get claims returning 500 for invalid token format
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}


