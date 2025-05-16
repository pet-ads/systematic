package br.all.protocol.controller

import br.all.application.protocol.find.FindProtocolServiceImpl
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.update.UpdateProtocolServiceImpl
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
    ) = FindProtocolServiceImpl(protocolRepository, systematicStudyRepository, credentialsService)

    @Bean
    fun updateProtocolService(
        protocolRepository: ProtocolRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService,
        studyReviewRepository: StudyReviewRepository,
        scoreCalculatorService: ScoreCalculatorService
    ) = UpdateProtocolServiceImpl(protocolRepository, systematicStudyRepository, credentialsService, studyReviewRepository, scoreCalculatorService)
}
