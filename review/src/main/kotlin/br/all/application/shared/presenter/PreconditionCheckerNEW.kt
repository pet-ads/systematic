package br.all.application.shared.presenter

import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UnauthenticatedUserException
import br.all.domain.shared.exception.UnauthorizedUserException
import br.all.domain.shared.user.Researcher
import br.all.domain.shared.user.Role
import br.all.domain.shared.user.Role.ADMIN
import br.all.domain.shared.user.Role.COLLABORATOR
import br.all.domain.model.review.SystematicStudy
import br.all.domain.shared.presenter.GenericPresenter


fun GenericPresenter<*>.prepareIfFailsPreconditions(
    user: Researcher?,
    systematicStudy: SystematicStudy?,
    allowedRoles: Set<Role> = setOf(COLLABORATOR)
) {
    this.prepareIfUnauthorized(user, allowedRoles)
    if (this.isDone()) return

    val existingUser = user!!

    if (systematicStudy == null) {
        this.prepareFailView(EntityNotFoundException("Review does not exists."))
        return
    }

    if (allowedRoles.contains(ADMIN) && existingUser.roles.contains(ADMIN)) return

    if (!systematicStudy.collaborators.contains(existingUser.id))
        this.prepareFailView(UnauthorizedUserException("User of id $existingUser can not perform this action."))
}


fun GenericPresenter<*>.prepareIfUnauthorized(
    user: Researcher?,
    allowedRoles: Set<Role> = setOf(COLLABORATOR)
) {
    if (user == null) {
        prepareFailView(UnauthenticatedUserException("Current user is not authenticated."))
        return
    }
    if (hasAnyOfRequiredRoles(user, allowedRoles)) {
        val message = "Authenticated user ${user.id} has none of required roles: ${allowedRoles.joinToString()}"
        prepareFailView(UnauthorizedUserException(message))
    }
}

private fun hasAnyOfRequiredRoles(
    user: Researcher,
    allowedRoles: Set<Role>
) = user.roles.intersect(allowedRoles).isEmpty()
