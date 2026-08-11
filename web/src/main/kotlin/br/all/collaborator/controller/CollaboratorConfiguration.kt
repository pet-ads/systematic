package br.all.collaborator.controller

import br.all.application.collaborator.find.FindAllCollaboratorsServiceImpl
import br.all.application.collaborator.find.FindCollaboratorRoleServiceImpl
import br.all.application.collaborator.find.SearchCollaboratorCandidatesServiceImpl
import br.all.application.collaborator.invitation.AddCollaboratorService
import br.all.application.collaborator.invitation.RespondInvitationServiceImpl
import br.all.application.collaborator.leave.LeaveSystematicStudyServiceImpl
import br.all.application.collaborator.remove.RemoveCollaboratorServiceImpl
import br.all.application.collaborator.update.PassOwnershipServiceImpl
import br.all.application.collaborator.repository.CollaboratorRepository
import br.all.application.collaborator.repository.CollaboratorTokenRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.collaborator.update.UpdateResearcherRoleServiceImpl
import br.all.application.shared.service.AuthorizationService
import br.all.application.user.SearchResearchesService
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

    @Bean
    fun findCollaboratorRole(
        authorizationService: AuthorizationService,
    ) = FindCollaboratorRoleServiceImpl(
        authorizationService,
    )

    @Bean
    fun respondInvitationService(
        tokenRepository: CollaboratorTokenRepository,
        addCollaboratorService: AddCollaboratorService,
    ) = RespondInvitationServiceImpl(tokenRepository, addCollaboratorService)

    @Bean
    fun searchCollaboratorCandidates(
        searchResearchesService: SearchResearchesService,
        systematicStudyRepository: SystematicStudyRepository,
        collaboratorTokenRepository: CollaboratorTokenRepository,
    ) = SearchCollaboratorCandidatesServiceImpl(searchResearchesService,systematicStudyRepository, collaboratorTokenRepository)

    @Bean
    fun updateResearcherRoleService(
        collaboratorRepository: CollaboratorRepository,
        authorizationService: AuthorizationService,
    ) = UpdateResearcherRoleServiceImpl(collaboratorRepository, authorizationService)
}
