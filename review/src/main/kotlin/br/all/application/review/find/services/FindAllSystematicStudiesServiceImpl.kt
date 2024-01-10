package br.all.application.review.find.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.find.services.FindAllSystematicStudiesService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.researcher.ResearcherId
import java.util.*

class FindAllSystematicStudiesServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService,
): FindAllSystematicStudiesService {
    override fun findAll(presenter: FindAllSystematicStudyPresenter, researcherId: UUID) {
        if (researcherNotAllowed(presenter, researcherId)) return

        systematicStudyRepository.findSomeByCollaborator(researcherId).let {
            presenter.prepareSuccessView(ResponseModel(researcherId, it))
        }
    }

    override fun findAllByOwner(presenter: FindAllSystematicStudyPresenter, researcherId: UUID, ownerId: UUID) {
        if (researcherNotAllowed(presenter, researcherId)) return

        systematicStudyRepository.findSomeByCollaboratorAndOwner(researcherId, ownerId).let {
            val response = ResponseModel(
                researcherId = researcherId,
                ownerId = ownerId,
                systematicStudies = it
            )
            presenter.prepareSuccessView(response)
        }
    }

    private fun researcherNotAllowed(
        presenter: FindAllSystematicStudyPresenter,
        researcherId: UUID,
    ) = PreconditionChecker(systematicStudyRepository, credentialsService).run {
            prepareIfUnauthenticatedOrUnauthorized(presenter, ResearcherId(researcherId))
            presenter.isDone()
    }
}