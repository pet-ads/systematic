package br.all.application.review.update.services

import br.all.application.review.repository.CollaboratorRepository
import br.all.application.review.update.presenter.UpdateResearcherRolePresenter
import br.all.application.review.update.services.UpdateResearcherRoleService.RequestModel
import br.all.application.review.update.services.UpdateResearcherRoleService.ResponseModel
import br.all.application.shared.service.AuthorizationService
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.user.Role

class UpdateResearcherRoleServiceImpl(
    private val collaboratorRepository: CollaboratorRepository,
    private val authorizationService: AuthorizationService
    ): UpdateResearcherRoleService {
    private val assignableRoles = setOf(Role.VIEWER, Role.REVIEWER, Role.EDITOR)

    override fun update(
        presenter: UpdateResearcherRolePresenter,
        request: RequestModel
    ) {
        authorizationService.authorize(presenter, request.userId, request.systematicStudyId, setOf(Role.OWNER))
        if (presenter.isDone()) return

        if (request.role !in assignableRoles) {
            presenter.prepareFailView(IllegalArgumentException("Role ${request.role} cannot be assigned to a collaborator."))
            return
        }

        if (request.researcherId == request.userId) {
            presenter.prepareFailView(IllegalArgumentException("You cannot self switch your own role"))
            return
        }

        val researcher = collaboratorRepository.findByResearcherIdAndSystematicStudyId(request.researcherId, request.systematicStudyId)
        if(researcher == null) {
            presenter.prepareFailView(EntityNotFoundException("There is no collaborator of id ${request.researcherId} on this systematic study."))
            return
        }

        researcher.role = request.role.toString()
        collaboratorRepository.saveOrUpdate(researcher)

        presenter.prepareSuccessView(ResponseModel(request.researcherId, request.role))
    }
}