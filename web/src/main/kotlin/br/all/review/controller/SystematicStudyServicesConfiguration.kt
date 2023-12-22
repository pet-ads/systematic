package br.all.review.controller

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.create.CreateSystematicStudyServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.services.UuidGeneratorService
import org.springframework.context.annotation.Configuration

@Configuration
class SystematicStudyServicesConfiguration {
    fun createSystematicStudyService(
        systematicStudyRepository: SystematicStudyRepository,
        uuidGeneratorService: UuidGeneratorService,
        credentialsService: ResearcherCredentialsService,
    ) = CreateSystematicStudyServiceImpl(systematicStudyRepository, uuidGeneratorService, credentialsService)
}