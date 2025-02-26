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

        // TODO as classes agora estão no pacote utils/example
        val password = encoder.encode("admin")
        val lucasUserAccount = register.registerUserAccount("buenolro", password)
        val systematicId = create.createReview(lucasUserAccount.id.value(), setOf(lucasUserAccount.id.value()))

        //TODO criar as questões não como textual apenas. Crie algumas como pickone, com os valores da seção 3.3 do documento.
        //TODO Insira as questões antes do protocolo (talvez dentro da classe CreateSystematicReviewExampleService, assim você vai ter os UUIDs para usar.
        //TODO Todas as questões aqui são extraction. Se precisar, invente uma para ROB, só para voltar na UI.

        val rq1 = question.createQuestion(
            reviewId = systematicId,
            code = "RQ1",
            title = "How has service-orientation been applied to the development of robotic systems?",
            context = QuestionContextEnum.EXTRACTION
        )

        val rq2 = question.createQuestion(
            reviewId = systematicId,
            code = "RQ2",
            title = "What is the most common way of interaction among service-oriented robotic systems?",
            context = QuestionContextEnum.EXTRACTION
        )

        val rq3 = question.createQuestion(
            reviewId = systematicId,
            code = "RQ3",
            title = "What implementation technology has been mostly used to develop service-oriented robotic systems?",
            context = QuestionContextEnum.EXTRACTION
        )

        val rq4 = question.createQuestion(
            reviewId = systematicId,
            code = "RQ4",
            title = "What are the development environments and tools that support the development of service-oriented robotic systems?",
            context = QuestionContextEnum.EXTRACTION
        )

        val rq5 = question.createQuestion(
            reviewId = systematicId,
            code = "RQ56",
            title = "Is SOA applicable to all types of robots?",
            context = QuestionContextEnum.EXTRACTION
        )

        val rq6 = question.createQuestion(
            reviewId = systematicId,
            code = "RQ6",
            title = "How has Software Engineering been applied to the development of service-oriented robotic systems?",
            context = QuestionContextEnum.EXTRACTION
        )

        //TODO o arquivo ALL.bib está na pasta resources. Ao invés de usar um só, insira um para cada base, criando
        //TODO search sessions realistas (com a string adaptada, campo de observação, data da busca, etc.)
        search.convert(systematicId.toSystematicStudyId(), lucasUserAccount.id.value(), "ALL.bib", "Scopus")
    }
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}
