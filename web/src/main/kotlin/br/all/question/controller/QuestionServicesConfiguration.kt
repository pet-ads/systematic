package br.all.question.controller

import br.all.application.question.create.CreateQuestionServiceImpl
import br.all.application.question.find.FindQuestionServiceImpl
import br.all.application.question.findAll.FindAllBySystematicStudyIdServiceImpl
import br.all.application.question.repository.QuestionRepository
import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.services.UuidGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuestionServicesConfiguration {
    @Bean
    fun createQuestionService(
        systematicStudyRepository: SystematicStudyRepository,
        questionRepository: QuestionRepository,
        credentialsService: ResearcherCredentialsService,
        idGenerator: UuidGeneratorService,
    ) = CreateQuestionServiceImpl(
        systematicStudyRepository,
        questionRepository,
        idGenerator,
        credentialsService
    )

    @Bean
    fun findQuestionService(
        systematicStudyRepository: SystematicStudyRepository,
        questionRepository: QuestionRepository,
        credentialsService: ResearcherCredentialsService
    ) = FindQuestionServiceImpl(
        systematicStudyRepository, questionRepository, credentialsService
    )

    @Bean
    fun findAllBySystematicStudyIdService(
        systematicStudyRepository: SystematicStudyRepository,
        questionRepository: QuestionRepository,
        credentialsService: ResearcherCredentialsService
    ) = FindAllBySystematicStudyIdServiceImpl(
        systematicStudyRepository, questionRepository, credentialsService
    )
}