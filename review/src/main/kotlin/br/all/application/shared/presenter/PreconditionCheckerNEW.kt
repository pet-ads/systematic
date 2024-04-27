package br.all.application.shared.presenter

import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.domain.model.researcher.Researcher
import br.all.domain.model.researcher.Role
import br.all.domain.model.researcher.Role.ADMIN
import br.all.domain.model.researcher.Role.RESEARCHER
import br.all.domain.model.review.SystematicStudy


fun GenericPresenter<*>.prepareIfFailsPreconditions(
    user: Researcher?,
    allowedRoles: Set<Role> = setOf(RESEARCHER),
    systematicStudy: SystematicStudy?
) {
    this.prepareIfUnauthorized(user, allowedRoles)
    if (this.isDone()) return

    val existingUser = user!!

    if (systematicStudy == null) {
        this.prepareFailView(EntityNotFoundException("Review does not exists."))
        return
    }
    val isAdminAction = existingUser.roles.contains(ADMIN) && allowedRoles.contains(ADMIN)
    if (!isAdminAction || !systematicStudy.collaborators.contains(existingUser.id))
        this.prepareFailView(UnauthorizedUserException("User of id $existingUser can not perform this action."))
}


fun GenericPresenter<*>.prepareIfUnauthorized(
    user: Researcher?,
    allowedRoles: Set<Role> = setOf(RESEARCHER)
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
