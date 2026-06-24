package br.all.application.review.find.services

import br.all.application.review.find.presenter.SearchCollaboratorCandidatesPresenter
import br.all.application.review.find.services.SearchCollaboratorCandidatesService.RequestModel
import br.all.application.review.find.services.SearchCollaboratorCandidatesService.ResponseModel
import br.all.application.user.SearchResearchesService
import br.all.domain.shared.exception.EntityNotFoundException

class SearchCollaboratorCandidatesServiceImpl(
    private val searchResearchesService: SearchResearchesService,
): SearchCollaboratorCandidatesService {
    override fun findCandidatesWith(
        presenter: SearchCollaboratorCandidatesPresenter,
        request: RequestModel
    ) {
        if (request.prefix.length < 3) {
            presenter.prepareFailView(IllegalArgumentException("Prefix with less than 3 characters is not allowed ${request.prefix}"))
            return
        }

        val users = searchResearchesService.searchUsers(request.prefix)

        if (users.isEmpty()) {
            presenter.prepareFailView(EntityNotFoundException("No users found with this prefix ${request.prefix}"))
            return
        }

        presenter.prepareSuccessView(ResponseModel(users))
    }
}