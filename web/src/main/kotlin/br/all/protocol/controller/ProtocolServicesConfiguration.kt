package br.all.protocol.controller

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.protocol.find.FindProtocolServiceImpl
import br.all.application.protocol.find.GetProtocolStageServiceImpl
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.update.UpdateProtocolServiceImpl
import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.services.ScoreCalculatorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProtocolServicesConfiguration {
    @Bean
    fun createFindOneProtocolService(
        protocolRepository: ProtocolRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = FindProtocolServiceImpl(protocolRepository, systematicStudyRepository, credentialsService, collaborationRepository)

    @Bean
    fun updateProtocolService(
        protocolRepository: ProtocolRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService,
        studyReviewRepository: StudyReviewRepository,
        scoreCalculatorService: ScoreCalculatorService,
        collaborationRepository: CollaborationRepository
    ) = UpdateProtocolServiceImpl(
        protocolRepository,
        systematicStudyRepository,
        credentialsService,
        studyReviewRepository,
        scoreCalculatorService,
        collaborationRepository
    )

    @Bean
    fun getProtocolStageService(
        protocolRepository: ProtocolRepository,
        systematicStudyRepository: SystematicStudyRepository,
        studyReviewRepository: StudyReviewRepository,
        credentialsService: CredentialsService,
        questionRepository: QuestionRepository,
        collaborationRepository: CollaborationRepository
    ) = GetProtocolStageServiceImpl(
        protocolRepository,
        systematicStudyRepository,
        studyReviewRepository,
        credentialsService,
        questionRepository,
        collaborationRepository
    )
}
