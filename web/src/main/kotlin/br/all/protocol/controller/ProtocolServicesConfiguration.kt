package br.all.protocol.controller

import br.all.application.protocol.find.FindProtocolServiceImpl
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.update.UpdateProtocolServiceImpl
import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProtocolServicesConfiguration {
    @Bean
    fun createFindOneProtocolService(
        protocolRepository: ProtocolRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: ResearcherCredentialsService,
    ) = FindProtocolServiceImpl(protocolRepository, systematicStudyRepository, credentialsService)

    @Bean
    fun updateProtocolService(
        protocolRepository: ProtocolRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: ResearcherCredentialsService,
    ) = UpdateProtocolServiceImpl(protocolRepository, systematicStudyRepository, credentialsService)
}
