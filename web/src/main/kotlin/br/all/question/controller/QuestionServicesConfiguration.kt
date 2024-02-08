package br.all.question.controller

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.question.create.CreateQuestionServiceImpl
import br.all.application.question.create.CreateQuestionStrategy
import br.all.application.question.find.FindQuestionServiceImpl
import br.all.application.question.repository.QuestionRepository
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.services.UuidGeneratorService
import br.all.application.question.shared.QuestionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuestionServicesConfiguration {
    @Bean
    fun createQuestionService(
        systematicStudyRepository: SystematicStudyRepository,
        questionFactory: QuestionFactory,
        protocolRepository: ProtocolRepository,
        credentialsService: ResearcherCredentialsService,
        idGenerator: UuidGeneratorService,
        strategy: CreateQuestionStrategy
    ) = CreateQuestionServiceImpl(questionFactory, systematicStudyRepository, strategy, idGenerator, credentialsService)

    @Bean
    fun findQuestionService(
        systematicStudyRepository: SystematicStudyRepository,
        questionRepository: QuestionRepository,
        credentialsService: ResearcherCredentialsService
    ) = FindQuestionServiceImpl(
        systematicStudyRepository, questionRepository, credentialsService
    )

    @Bean
    fun questionFactory()
}