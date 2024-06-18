package br.all.review.controller

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.user.CredentialsService
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
        credentialsService: CredentialsService,
    ) = FindSystematicStudyServiceImpl(systematicStudyRepository, credentialsService)

    @Bean
    fun findAllSystematicStudiesService(
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService,
    ) = FindAllSystematicStudiesServiceImpl(systematicStudyRepository, credentialsService)

    @Bean
    fun updateSystematicStudyService(
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService,
    ) = UpdateSystematicStudyServiceImpl(systematicStudyRepository, credentialsService)
}
