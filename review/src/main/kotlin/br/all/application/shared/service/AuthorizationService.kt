package br.all.application.shared.service

import br.all.application.collaborator.repository.CollaboratorRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.shared.exception.EntityNotFoundException
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
        val systematicStudy: SystematicStudy,
        val role: Role,
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
        var role: Role? = null


        if (systematicStudy == null) {
            presenter.prepareFailView(EntityNotFoundException("Review does not exists."))
            return null
        }

        if (researcher != null) {
            if (researcher.roles.contains(Role.ADMIN)) {
                role = Role.ADMIN
            } else {
                val collaborator = collaboratorRepository.findByResearcherIdAndSystematicStudyId(
                    researcherId,
                    systematicStudyId
                )


                collaborator?.let {
                    role = Role.valueOf(it.role)
                }

                researcher.roles.clear()

                if (role != null) researcher.roles.add(role)
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
            systematicStudy = systematicStudy,
            role = role!!
        )
    }
}