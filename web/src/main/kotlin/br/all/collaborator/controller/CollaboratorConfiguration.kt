package br.all.collaborator.controller

import br.all.application.collaborator.find.FindAllCollaboratorsServiceImpl
import br.all.application.collaborator.leave.LeaveSystematicStudyServiceImpl
import br.all.application.collaborator.remove.RemoveCollaboratorServiceImpl
import br.all.application.collaborator.update.PassOwnershipServiceImpl
import br.all.application.review.repository.CollaboratorRepository
import br.all.application.review.repository.CollaboratorTokenRepository
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

    @Bean
    fun leaveSystematicStudyService(
        collaboratorRepository: CollaboratorRepository,
        systematicStudyRepository: SystematicStudyRepository,
        authorizationService: AuthorizationService,
    ) = LeaveSystematicStudyServiceImpl(
        collaboratorRepository,
        systematicStudyRepository,
        authorizationService,
    )

    @Bean
    fun passOwnershipService(
        collaboratorRepository: CollaboratorRepository,
        systematicStudyRepository: SystematicStudyRepository,
        authorizationService: AuthorizationService,
    ) = PassOwnershipServiceImpl(
        collaboratorRepository,
        systematicStudyRepository,
        authorizationService,
    )

    @Bean
    fun findAllCollaborators(
        collaboratorTokenRepository: CollaboratorTokenRepository,
        collaboratorRepository: CollaboratorRepository,
        authorizationService: AuthorizationService,
    ) = FindAllCollaboratorsServiceImpl(
        collaboratorTokenRepository,
        collaboratorRepository,
        authorizationService,
    )
}
