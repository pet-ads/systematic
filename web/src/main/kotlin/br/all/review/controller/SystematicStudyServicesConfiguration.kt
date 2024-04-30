package br.all.review.controller

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.researcher.CredentialsService
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.create.CreateSystematicStudyServiceImpl
import br.all.application.review.find.services.FindAllSystematicStudiesServiceImpl
import br.all.application.review.find.services.FindSystematicStudyServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.update.services.UpdateSystematicStudyServiceImpl
import br.all.domain.services.UuidGeneratorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SystematicStudyServicesConfiguration {
    @Bean
    fun createSystematicStudyService(
        systematicStudyRepository: SystematicStudyRepository,
        protocolRepository: ProtocolRepository,
        uuidGeneratorService: UuidGeneratorService,
        credentialsService: CredentialsService,
    ) = CreateSystematicStudyServiceImpl(
        systematicStudyRepository,
        protocolRepository,
        uuidGeneratorService,
        credentialsService,
    )

    @Bean
    fun findOneSystematicStudyService(
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: ResearcherCredentialsService,
    ) = FindSystematicStudyServiceImpl(systematicStudyRepository, credentialsService)

    @Bean
    fun findAllSystematicStudiesService(
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: ResearcherCredentialsService,
    ) = FindAllSystematicStudiesServiceImpl(systematicStudyRepository, credentialsService)

    @Bean
    fun updateSystematicStudyService(
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: ResearcherCredentialsService,
    ) = UpdateSystematicStudyServiceImpl(systematicStudyRepository, credentialsService)
}
