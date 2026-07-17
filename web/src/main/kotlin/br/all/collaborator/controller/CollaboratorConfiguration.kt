package br.all.collaborator.controller

import br.all.application.collaborator.remove.RemoveCollaboratorServiceImpl
import br.all.application.review.repository.CollaboratorRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.service.AuthorizationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CollaboratorConfiguration {
    @Bean
    fun removeCollaboratorService(
        collaboratorRepository: CollaboratorRepository,
        systematicStudyRepository: SystematicStudyRepository,
        authorizationService: AuthorizationService,
    ) = RemoveCollaboratorServiceImpl(
        collaboratorRepository,
        systematicStudyRepository,
        authorizationService,
    )
}
