package br.all.collaboration.controller

import br.all.application.collaboration.create.AcceptInviteServiceImpl
import br.all.application.collaboration.create.CreateInviteServiceImpl
import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.user.CredentialsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CollaborationServicesConfiguration {
    
    @Bean
    fun createInviteService(
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = CreateInviteServiceImpl(
        systematicStudyRepository, credentialsService, collaborationRepository
    )

    @Bean
    fun acceptInviteService(
        systematicStudyRepository: SystematicStudyRepository,
        credentialsService: CredentialsService,
        collaborationRepository: CollaborationRepository
    ) = AcceptInviteServiceImpl(
        systematicStudyRepository, credentialsService, collaborationRepository
    )
}