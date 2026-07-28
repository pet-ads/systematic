package br.all.application.collaborator.find

import br.all.application.collaborator.repository.CollaboratorTokenRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.user.SearchResearchesService
import br.all.domain.shared.exception.EntityNotFoundException

class SearchCollaboratorCandidatesServiceImpl(
    private val searchResearchesService: SearchResearchesService,
    private val systematicRepository: SystematicStudyRepository,
    private val collaboratorTokenRepository: CollaboratorTokenRepository,
): SearchCollaboratorCandidatesService {
    override fun findCandidatesWith(
        presenter: SearchCollaboratorCandidatesPresenter,
        request: SearchCollaboratorCandidatesService.RequestModel
    ) {
        val users = searchResearchesService.searchUsers(request.prefix)

        if (users.isEmpty()) {
                presenter.prepareFailView(EntityNotFoundException("No eligible users found matching ${request.prefix}"))
            return
        }

        val systematicStudy = systematicRepository.findById(request.systematicStudyId)

        if (systematicStudy === null) {
            val message = "There is no systematic study of id ${request.systematicStudyId}."
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        val invitedResearchers = collaboratorTokenRepository.findAllBySystematicStudyId(request.systematicStudyId)

        val collaborators = systematicStudy.collaborators

        val filteredResearchers = users.filter { user ->
            collaborators.none { collaborator ->
                collaborator == user.id
            }
        }.filter { it ->
            it.id !in invitedResearchers.map { it.researcherId }
        }

        presenter.prepareSuccessView(SearchCollaboratorCandidatesService.ResponseModel(filteredResearchers))
    }
}