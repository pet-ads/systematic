package br.all.application.shared.presenter

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.ReviewId
import java.util.*

class PreconditionChecker(
    private val reviewRepository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService
) {

    fun prepareIfViolates(presenter: GenericPresenter<*>, reviewId: ReviewId, researcherId: ResearcherId) {
        if (!credentialsService.isAuthenticated(researcherId))
            presenter.prepareFailView(UnauthenticatedUserException("User of id $researcherId is not authenticated."))

        if (!reviewRepository.existsById(reviewId.value))
            presenter.prepareFailView(EntityNotFoundException("Review of id $reviewId do not exists."))

        if (!reviewRepository.hasReviewer(reviewId.value, researcherId.value))
            presenter.prepareFailView(UnauthorizedUserException("User of id $researcherId is not a reviewer."))

    }
}