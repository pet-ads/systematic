package br.all.application.review.find.services

import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.find.services.FindAllSystematicStudiesService.FindByOwnerRequest
import br.all.application.review.find.services.FindAllSystematicStudiesService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.researcher.ResearcherId
import java.util.*

class FindAllSystematicStudiesServiceImpl(
    private val repository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService,
) : FindAllSystematicStudiesService {

    override fun findAllByCollaborator(presenter: FindAllSystematicStudyPresenter, researcher: UUID) {
        if (researcherNotAllowed(presenter, researcher)) return

        repository.findAllByCollaborator(researcher).let {
            presenter.prepareSuccessView(ResponseModel(researcher, it))
        }
    }

    override fun findAllByOwner(presenter: FindAllSystematicStudyPresenter, request: FindByOwnerRequest) {
        val (researcher, owner) = request
        if (researcherNotAllowed(presenter, researcher)) return

        repository.findAllByCollaboratorAndOwner(researcher, owner).let {
            ResponseModel(researcherId = researcher, ownerId = owner, systematicStudies = it).also { response ->
                presenter.prepareSuccessView(response)
            }
        }
    }

    private fun researcherNotAllowed(
        presenter: FindAllSystematicStudyPresenter,
        researcher: UUID,
    ) = PreconditionChecker(repository, credentialsService).run {
        prepareIfUnauthenticatedOrUnauthorized(presenter, ResearcherId(researcher))
        presenter.isDone()
    }
}
