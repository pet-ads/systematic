package br.all.application.shared.service

import br.all.application.review.repository.CollaboratorRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.shared.presenter.GenericPresenter
import br.all.domain.shared.user.Researcher
import br.all.domain.shared.user.Role
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthorizationService(
    private val credentialsService: CredentialsService,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val collaboratorRepository: CollaboratorRepository
) {

    data class AuthorizationContext(
        val researcher: Researcher,
        val systematicStudy: SystematicStudy
    )

    fun authorize(
        presenter: GenericPresenter<*>,
        researcherId: UUID,
        systematicStudyId: UUID,
        allowedRoles: Set<Role> = setOf(Role.EDITOR)
    ): AuthorizationContext? {

        val researcher = credentialsService.loadCredentials(researcherId)?.toUser()
        val systematicStudyDto = systematicStudyRepository.findById(systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        if (researcher != null &&
            systematicStudy != null &&
            !researcher.roles.contains(Role.ADMIN))
        {
            val collaborator = collaboratorRepository.findByResearcherIdAndSystematicStudyId(
                researcherId,
                systematicStudyId
            )

            if (collaborator != null) {
                researcher.roles += Role.valueOf(collaborator.role)
            }
        }

        presenter.prepareIfFailsPreconditions(
            researcher,
            systematicStudy,
            allowedRoles
        )

        if (presenter.isDone()) {
            return null
        }

        return AuthorizationContext(
            researcher = researcher!!,
            systematicStudy = systematicStudy!!
        )
    }
}