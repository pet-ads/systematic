package br.all.application.shared.presenter

import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.domain.shared.exception.UnauthenticatedUserException
import br.all.domain.shared.exception.UnauthorizedUserException
import br.all.domain.model.user.ResearcherId
import br.all.domain.model.review.SystematicStudyId

class PreconditionChecker(
    private val reviewRepository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService,
) {

    fun prepareIfViolatesPreconditions(presenter: GenericPresenter<*>, researcherId: ResearcherId, systematicStudyId: SystematicStudyId) {
        prepareIfUnauthenticatedOrUnauthorized(presenter, researcherId)
        if (presenter.isDone())
            return
        if (!reviewRepository.existsById(systematicStudyId.value())) {
            presenter.prepareFailView(EntityNotFoundException("Review of id $systematicStudyId do not exists."))
            return
        }
        if (!reviewRepository.hasReviewer(systematicStudyId.value(), researcherId.value))
            presenter.prepareFailView(UnauthorizedUserException("User of id $researcherId is not a reviewer."))
    }



    fun prepareIfUnauthenticatedOrUnauthorized(presenter: GenericPresenter<*>, researcherId: ResearcherId) {
        if (!credentialsService.isAuthenticated(researcherId)) {
            presenter.prepareFailView(UnauthenticatedUserException("User of id $researcherId is not authenticated."))
            return
        }
        if (!credentialsService.hasAuthority(researcherId)) {
            presenter.prepareFailView(UnauthorizedUserException("User of id $researcherId is not authorized."))
            return
        }
    }
}