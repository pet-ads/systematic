package br.all.question.controller

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.question.create.CreateQuestionServiceImpl
import br.all.application.question.find.FindQuestionServiceImpl
import br.all.application.question.findAll.FindAllBySystematicStudyIdServiceImpl
import br.all.application.question.repository.QuestionRepository
import br.all.application.question.update.presenter.UpdateQuestionPresenter
import br.all.application.question.update.services.UpdateQuestionServiceImpl
import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.user.CredentialsService
import br.all.domain.services.UuidGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuestionServicesConfiguration {
    @Bean
    fun createQuestionService(
        systematicStudyRepository: SystematicStudyRepository,
        questionRepository: QuestionRepository,
        credentialsService: CredentialsService,
        idGenerator: UuidGeneratorService,
        collaborationRepository: CollaborationRepository
    ) = CreateQuestionServiceImpl(
        systematicStudyRepository,
        questionRepository,
        idGenerator,
        credentialsService,
        collaborationRepository
    )

    @Bean
    fun findQuestionService(
        systematicStudyRepository: SystematicStudyRepository,
        questionRepository: QuestionRepository,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = FindQuestionServiceImpl(
        systematicStudyRepository, questionRepository, credentialsService, collaborationRepository
    )

    @Bean
    fun findAllBySystematicStudyIdService(
        systematicStudyRepository: SystematicStudyRepository,
        questionRepository: QuestionRepository,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = FindAllBySystematicStudyIdServiceImpl(
        systematicStudyRepository, questionRepository, credentialsService, collaborationRepository
    )

    @Bean
    fun updateQuestionService(
        systematicStudyRepository: SystematicStudyRepository,
        questionRepository: QuestionRepository,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = UpdateQuestionServiceImpl(
        systematicStudyRepository, questionRepository, credentialsService, collaborationRepository
    )
}