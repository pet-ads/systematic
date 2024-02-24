package br.all.protocol.controller

import br.all.application.protocol.create.CreateProtocolServiceImpl
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProtocolServicesConfiguration {
    @Bean
    fun createProtocolService(
        protocolRepository: ProtocolRepository,
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: ResearcherCredentialsService,
    ) = CreateProtocolServiceImpl(protocolRepository, systematicStudyRepository, credentialsService)
}
