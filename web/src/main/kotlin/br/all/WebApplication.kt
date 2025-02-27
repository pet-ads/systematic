package br.all

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
import java.time.LocalDateTime


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


        search.convert(
            systematicStudyId = systematicId.toSystematicStudyId(),
            userId = lucasUserAccount.id.value(),
            bibFileName = "Springer.bib",
            sourceName = "Springer",
            timestamp = LocalDateTime.of(2022, 9, 20, 16, 45),
            searchString = """
        ab:(("Service Oriented" OR "Service-oriented") AND (Robot OR Robotic OR humanoid))
    """.trimIndent(),
            additionalInformation = "Springer search performed on 2022-09-20 using complementary substrings (example shown above). Returned 11 studies after duplicate filtering. Only English studies were considered."
        )

        //TODO i dont know why specifically web of science and springer arent working properly
        search.convert(
            systematicStudyId = systematicId.toSystematicStudyId(),
            userId = lucasUserAccount.id.value(),
            bibFileName = "WebOfScience.bib",
            sourceName = "Web of Science",
            timestamp = LocalDateTime.of(2022, 9, 20, 17, 0),
            searchString = """
        Topic=(("Service Oriented" OR "Service-oriented" OR "Service Based" OR "Service-based" OR "Service Orientation" OR SOA)
        AND (Robot OR Robotic OR humanoid))
        OR
        Title=(("Service Oriented" OR "Service-oriented" OR "Service Based" OR "Service-based" OR "Service Orientation" OR SOA)
        AND (Robot OR Robotic OR humanoid))
    """.trimIndent(),
            additionalInformation = "Web of Science search performed on 2022-09-20 using both Topic and Title fields. Returned 80 studies. Only English studies were considered."
        )

        search.convert(
            systematicStudyId = systematicId.toSystematicStudyId(),
            userId = lucasUserAccount.id.value(),
            bibFileName = "ACM.bib",
            sourceName = "ACM Digital Library",
            timestamp = LocalDateTime.of(2022, 9, 20, 15, 30),
            searchString = """
        Abstract:(("Service Oriented" OR "Service-oriented" OR "Service Based" OR "Service-based" OR "Service Orientation" OR SOA) 
        AND (Robot OR Robotic OR humanoid))
    """.trimIndent(),
            additionalInformation = "ACM Digital Library search was performed on 2022-09-20. Query split into two parts (abstract and title); returned 5 and 1 results respectively. Only English studies were considered.",
        )

        search.convert(
            systematicStudyId = systematicId.toSystematicStudyId(),
            userId = lucasUserAccount.id.value(),
            bibFileName = "Compendex.bib",
            sourceName = "Compendex",
            timestamp = LocalDateTime.of(2022, 9, 20, 15, 45),
            searchString = """
        ((("Service Oriented" OR "Service-oriented" OR "Service Based" OR "Service-based" OR "Service Orientation" OR SOA) 
        AND (Robot OR Robotic OR humanoid)) WN KY), English only
    """.trimIndent(),
            additionalInformation = "Compendex search was performed on 2022-09-20 using the specified query applied to the keywords (WN KY) field. Only studies in English were considered.",
        )

        search.convert(
            systematicStudyId = systematicId.toSystematicStudyId(),
            userId = lucasUserAccount.id.value(),
            bibFileName = "IEEE.bib",
            sourceName = "IEEE Xplore",
            timestamp = LocalDateTime.of(2022, 9, 20, 16, 0),
            searchString = """
        ("Abstract":"Service Oriented" OR "Abstract":"Service-oriented" OR "Abstract":"Service Based" OR "Abstract":"Service-based" OR "Abstract":"Service Orientation" OR "Abstract":SOA)
        AND ("Abstract":Robot OR "Abstract":Robotic OR "Abstract":humanoid)
    """.trimIndent(),
            additionalInformation = "IEEE Xplore search performed on 2022-09-20 using separate queries for abstract and title. The abstract query returned 72 studies and the title query returned 22 studies. Only studies in English were considered."
        )

        search.convert(
            systematicStudyId = systematicId.toSystematicStudyId(),
            userId = lucasUserAccount.id.value(),
            bibFileName = "science.bib",
            sourceName = "ScienceDirect",
            timestamp = LocalDateTime.of(2022, 9, 20, 16, 15),
            searchString = """
        TITLE-ABSTR-KEY(("Service Oriented" OR "Service-oriented" OR "Service Based" OR "Service-based" OR "Service Orientation" OR SOA)
        AND (Robot OR Robotic OR humanoid))
    """.trimIndent(),
            additionalInformation = "ScienceDirect search performed on 2022-09-20 using the TITLE-ABSTR-KEY field. Returned 4 studies. Only English studies were considered."
        )

        search.convert(
            systematicStudyId = systematicId.toSystematicStudyId(),
            userId = lucasUserAccount.id.value(),
            bibFileName = "scopus.bib",
            sourceName = "Scopus",
            timestamp = LocalDateTime.of(2022, 9, 20, 16, 30),
            searchString = """
        TITLE-ABS-KEY(("Service Oriented" OR "Service-oriented" OR "Service Based" OR "Service-based" OR "Service Orientation" OR SOA)
        AND (robot OR robotic OR humanoid))
    """.trimIndent(),
            additionalInformation = "Scopus search performed on 2022-09-20 using the TITLE-ABS-KEY field. Returned 230 studies (duplicates filtered later). Only English studies were considered."
        )

    }
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}
