package br.all.application.review.find.services

import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.find.services.FindAllSystematicStudiesService.FindByOwnerRequest
import br.all.application.review.find.services.FindAllSystematicStudiesService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.prepareIfUnauthorized
import br.all.application.user.CredentialsService
import java.util.*

class FindAllSystematicStudiesServiceImpl(
    private val repository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
) : FindAllSystematicStudiesService {

    override fun findAllByCollaborator(presenter: FindAllSystematicStudyPresenter, user: UUID) {
        if (userNotAllowed(presenter, user)) return

        repository.findAllByCollaborator(user).let {
            presenter.prepareSuccessView(ResponseModel(user, it))
        }
    }

    override fun findAllByOwner(presenter: FindAllSystematicStudyPresenter, request: FindByOwnerRequest) {
        val (researcher, owner) = request
        if (userNotAllowed(presenter, researcher)) return

        repository.findAllByCollaboratorAndOwner(researcher, owner).let {
            ResponseModel(userId = researcher, ownerId = owner, systematicStudies = it).also { response ->
                presenter.prepareSuccessView(response)
            }
        }
    }

    private fun userNotAllowed(
        presenter: FindAllSystematicStudyPresenter,
        userId: UUID,
    ) = presenter.run {
        val user = credentialsService.loadCredentials(userId)?.toUser()
        presenter.prepareIfUnauthorized(user)
        presenter.isDone()
    }
}
