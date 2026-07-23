package br.all.application.collaborator.find

import br.all.application.shared.service.AuthorizationService
import br.all.domain.shared.user.Role

class FindCollaboratorRoleServiceImpl(
    private val authorizationService: AuthorizationService
): FindCollaboratorRoleService {
    override fun findRole(
        presenter: FindCollaboratorRolePresenter,
        request: FindCollaboratorRoleService.Request
    ) {
        val context = authorizationService.authorize(presenter, request.userId, request.systematicStudyId, setOf(Role.OWNER, Role.EDITOR, Role.VIEWER, Role.REVIEWER))
        if (presenter.isDone()) return
        context!!

        presenter.prepareSuccessView(FindCollaboratorRoleService.ResponseModel(context.role))
    }
}