package br.all

import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.model.review.toSystematicStudyId
import br.all.utils.example.CreateQuestionExampleService
import br.all.utils.example.CreateSystematicReviewExampleService
import br.all.utils.example.CreateSearchSessionExampleService
import br.all.utils.example.RegisterUserExampleService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.password.PasswordEncoder


@SpringBootApplication
class WebApplication {
    //TODO fix the token get claims returning 500 for invalid token format

    @Bean
    fun run(
        encoder: PasswordEncoder,
        register: RegisterUserExampleService,
        create: CreateSystematicReviewExampleService,
        search: CreateSearchSessionExampleService,
        question: CreateQuestionExampleService
    ) = CommandLineRunner {
        val password = encoder.encode("admin")
        val lucasUserAccount = register.registerUserAccount("buenolro", password)
        val systematicId = create.createReview(lucasUserAccount.id.value(), setOf(lucasUserAccount.id.value()))

        //TODO o arquivo ALL.bib está na pasta resources. Ao invés de usar um só, insira um para cada base, criando
        //TODO search sessions realistas (com a string adaptada, campo de observação, data da busca, etc.)
        search.convert(systematicId.toSystematicStudyId(), lucasUserAccount.id.value(), "ALL.bib", "Scopus")
    }
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}
