package br.all

import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.model.review.toSystematicStudyId
import br.all.usecases.CreateQuestionsUseCase
import br.all.usecases.CreateSystematicReviewUseCase
import br.all.usecases.NewSearchSessionUseCase
import br.all.usecases.RegisterUserUseCase
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
        register: RegisterUserUseCase,
        create: CreateSystematicReviewUseCase,
        search: NewSearchSessionUseCase,
        question: CreateQuestionsUseCase
    ) = CommandLineRunner {
        val password = encoder.encode("admin")

        val lucasUserAccount = register.registerUserAccount("buenolro", password)

        val systematicId = create.createReview(lucasUserAccount.id.value(), setOf(lucasUserAccount.id.value()))

        val robQuestion1 = question.createQuestion(
            reviewId = systematicId,
            code = "ROB1",
            title = "Was there any evidence of missing important primary studies due to limitations in the search strategy or database selection?",
            context = QuestionContextEnum.ROB
        )

        val robQuestion2 = question.createQuestion(
            reviewId = systematicId,
            code = "ROB2",
            title = "Were the reviewers' reliability and potential biases adequately addressed during study selection and data extraction?",
            context = QuestionContextEnum.ROB
        )

        val robQuestion3 = question.createQuestion(
            reviewId = systematicId,
            code = "ROB3",
            title = "Was a formal quality assessment of the included primary studies performed and were potential biases reported transparently?",
            context = QuestionContextEnum.ROB
        )

        val extractionQuestion1 = question.createQuestion(
            reviewId = systematicId,
            code = "EX1",
            title = "What are the main research questions or objectives addressed by the primary study?",
            context = QuestionContextEnum.EXTRACTION
        )

        val extractionQuestion2 = question.createQuestion(
            reviewId = systematicId,
            code = "EX2",
            title = "What service development approach (e.g., Sensors and Actuators, Tasks and Activities, Knowledge and Algorithms, or Whole Robot) is reported?",
            context = QuestionContextEnum.EXTRACTION
        )

        val extractionQuestion3 = question.createQuestion(
            reviewId = systematicId,
            code = "EX3",
            title = "Which implementation technologies (e.g., REST, WS-*, CORBA) and development environments are used in the study?",
            context = QuestionContextEnum.EXTRACTION
        )

        search.convert(systematicId.toSystematicStudyId(), lucasUserAccount.id.value())
    }
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}
