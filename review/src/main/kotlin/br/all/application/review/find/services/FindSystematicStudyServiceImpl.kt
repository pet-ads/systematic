package br.all.application.review.find.services

import br.all.application.review.find.presenter.FindSystematicStudyPresenter
import br.all.application.review.find.services.FindSystematicStudyService.RequestModel
import br.all.application.review.find.services.FindSystematicStudyService.ResponseModel
import br.all.application.collaborator.repository.CollaboratorRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfUnauthorized
import br.all.application.user.CredentialsService
import br.all.domain.shared.user.Role

class FindSystematicStudyServiceImpl(
    private val repository: SystematicStudyRepository,
    private val collaboratorRepository: CollaboratorRepository,
    private val credentialsService: CredentialsService,
) : FindSystematicStudyService {
    override fun findById(presenter: FindSystematicStudyPresenter, request: RequestModel) {
        val userId = request.userId

        val user = credentialsService.loadCredentials(userId)?.toUser()
        presenter.prepareIfUnauthorized(user)
        if (presenter.isDone()) return

        val systematicStudy = repository.findById(request.systematicStudy)

        if (systematicStudy === null) {
            val message = "There is no systematic study of id ${request.systematicStudy}."
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        val collaborator = collaboratorRepository.findByResearcherIdAndSystematicStudyId(userId, request.systematicStudy)

        if (collaborator === null) {
            val message = "You are not on systematic study ${request.systematicStudy}."
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        presenter.prepareSuccessView(ResponseModel(userId, request.systematicStudy, systematicStudy, Role.valueOf(
            collaborator.role
        )))
    }
}
