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

    //TODO Update RegisterUser endpoint with proper documentation
    //TODO finish video https://www.youtube.com/watch?v=_1npBylaRVs&list=PLvN8k8yxjoeud4ESoB-wjiieqYGaDVqPR&index=13
    //TODO write test cases
    
//    @Bean
//    fun run(repository: JpaAccountCredentialsRepository, encoder: PasswordEncoder) = CommandLineRunner { args ->
//        val user = UserAccountEntity(
//            uuid = UUID.randomUUID(),
//            username = "admin",
//            password = encoder.encode("password"), // Please use a strong password hashing mechanism in production!
//            email = "admin@example.com",
//            country = "US",
//            affiliation = "Example Corporation"
//        )
//        repository.save(user)
//    }
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}


