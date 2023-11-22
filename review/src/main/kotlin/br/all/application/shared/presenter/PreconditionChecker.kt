package br.all.application.shared.presenter

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.ReviewId

class PreconditionChecker(
    private val reviewRepository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService,
) {

    fun prepareIfViolatesPreconditions(presenter: GenericPresenter<*>, researcherId: ResearcherId, reviewId: ReviewId) {
        if (!credentialsService.isAuthenticated(researcherId))
            presenter.prepareFailView(UnauthenticatedUserException("User of id $researcherId is not authenticated."))

        if (!credentialsService.hasAuthority(researcherId))
            presenter.prepareFailView(UnauthenticatedUserException("User of id $researcherId is not authorized."))

        if (!reviewRepository.existsById(reviewId.value))
            presenter.prepareFailView(EntityNotFoundException("Review of id $reviewId do not exists."))

        if (!reviewRepository.hasReviewer(reviewId.value, researcherId.value))
            presenter.prepareFailView(UnauthorizedUserException("User of id $researcherId is not a reviewer."))
    }
}