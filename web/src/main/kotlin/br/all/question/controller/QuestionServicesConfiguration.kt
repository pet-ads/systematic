package br.all.question.controller

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.question.create.CreateQuestionServiceImpl
import br.all.application.question.find.FindQuestionServiceImpl
import br.all.application.question.repository.QuestionRepository
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.services.IdGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuestionServicesConfiguration {
    @Bean
    fun createQuestionService(
        systematicStudyRepository: SystematicStudyRepository,
        questionRepository: QuestionRepository,
        protocolRepository: ProtocolRepository,
        credentialsService: ResearcherCredentialsService,
        idGenerator: IdGeneratorService
    ) = CreateQuestionServiceImpl(
        questionRepository, systematicStudyRepository, protocolRepository, credentialsService
    )

    @Bean
    fun findReviewService(
        systematicStudyRepository: SystematicStudyRepository,
        questionRepository: QuestionRepository,
        credentialsService: ResearcherCredentialsService
    ) = FindQuestionServiceImpl(
        systematicStudyRepository, questionRepository, credentialsService
    )
}