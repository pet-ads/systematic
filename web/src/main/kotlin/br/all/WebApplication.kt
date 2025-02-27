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

        val eq1 = question.createQuestion(
            reviewId = systematicId,
            code = "EQ1",
            title = "How has service-orientation been applied to the development of robotic systems?",
            type = "TEXTUAL",
            context = QuestionContextEnum.EXTRACTION
        )

        val eq2 = question.createQuestion(
            reviewId = systematicId,
            code = "EQ2",
            title = "What is the most common way of interaction among service-oriented robotic systems?",
            type = "NUMBERED_SCALE",
            context = QuestionContextEnum.EXTRACTION,
            higher = 5,
            lower = 1
        )

        val eq3 = question.createQuestion(
            reviewId = systematicId,
            code = "EQ3",
            title = "What implementation technology has been mostly used to develop service-oriented robotic systems?",
            type = "LABELED_SCALE",
            context = QuestionContextEnum.EXTRACTION,
            scales = mapOf(
                "How clearly are the review’s research objectives stated?" to 1,
                "How comprehensive is the search strategy (databases, keywords, and search strings)?" to 2,
                "How transparent is the data extraction and synthesis process?" to 3,
                "How well are the findings (tables, graphs, mapping) organized and reported?" to 4,
                "How adequately are the threats to validity discussed?" to 5
            )
        )

        val eq4 = question.createQuestion(
            reviewId = systematicId,
            code = "EQ4",
            title = "Which research question provides the most valuable insight for future research?",
            type = "PICK_LIST",
            context = QuestionContextEnum.EXTRACTION,
            options = listOf(
                "RQ1: How has service-orientation been applied to the development of robotic systems?",
                "RQ2: What is the most common way of interaction among service-oriented robotic systems?",
                "RQ3: What implementation technology has been mostly used to develop service-oriented robotic systems?",
                "RQ4: What are the development environments and tools that support the development of service-oriented robotic systems?",
                "RQ5: Is SOA applicable to all types of robots?",
                "RQ6: How has Software Engineering been applied to the development of service-oriented robotic systems?"
            )
        )

        val rbq1 = question.createQuestion(
            reviewId = systematicId,
            code = "RBQ1",
            title = "In your own words, describe potential sources of selection bias in this review.",
            type = "TEXTUAL",
            context = QuestionContextEnum.ROB
        )

        val rbq2 = question.createQuestion(
            reviewId = systematicId,
            code = "RBQ2",
            title = "Which option best describes the overall risk of bias in the review?",
            type = "PICK_LIST",
            context = QuestionContextEnum.ROB,
            options = listOf(
                "Very High",
                "High",
                "Moderate",
                "Low",
                "Very Low"
            )
        )

        val rbq3 = question.createQuestion(
            reviewId = systematicId,
            code = "RBQ3",
            title = "How effectively is publication bias minimized (e.g., through multiple databases and specialist suggestions)?",
            type = "LABELED_SCALE",
            context = QuestionContextEnum.ROB,
            scales = mapOf(
                "Not effective at all" to 1,
                "Slightly effective" to 2,
                "Moderately effective" to 3,
                "Very effective" to 4,
                "Extremely effective" to 5
            )
        )

        val rbq4 = question.createQuestion(
            reviewId = systematicId,
            code = "RBQ4",
            title = "Rate the overall reliability of the review process from 1 (very unreliable) to 5 (very reliable).",
            type = "NUMBERED_SCALE",
            context = QuestionContextEnum.ROB,
            higher = 5,
            lower = 1
        )


        //TODO o arquivo ALL.bib está na pasta resources. Ao invés de usar um só, insira um para cada base, criando
        //TODO search sessions realistas (com a string adaptada, campo de observação, data da busca, etc.)
        search.convert(systematicId.toSystematicStudyId(), lucasUserAccount.id.value(), "ALL.bib", "Scopus")
    }
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}
