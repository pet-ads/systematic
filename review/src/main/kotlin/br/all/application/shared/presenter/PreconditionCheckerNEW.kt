package br.all.application.shared.presenter

import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.domain.model.collaboration.Collaboration
import br.all.domain.model.user.Researcher
import br.all.domain.model.user.Role
import br.all.domain.model.user.Role.ADMIN
import br.all.domain.model.user.Role.COLLABORATOR


fun GenericPresenter<*>.prepareIfFailsPreconditions(
    user: Researcher?,
    systematicStudy: br.all.domain.model.review.SystematicStudy?,
    allowedRoles: Set<Role> = setOf(COLLABORATOR),
    collaborations: List<Collaboration>?
) {
    this.prepareIfUnauthorized(user, allowedRoles)
    if (this.isDone()) return

    val existingUser = user!!

    if (systematicStudy == null) {
        this.prepareFailView(EntityNotFoundException("Review does not exists."))
        return
    }

    if (allowedRoles.contains(ADMIN) && existingUser.roles.contains(ADMIN)) return

    if(collaborations == null || collaborations.none{ it.userId == user.id }) 
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
