package br.all.application.review.find.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.find.presenter.FindAllSystematicStudyPresenter
import br.all.application.review.find.services.FindAllSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.researcher.ResearcherId
import java.util.*

class FindAllSystematicStudyServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService,
): FindAllSystematicStudyService {
    override fun findAll(presenter: FindAllSystematicStudyPresenter, researcherId: UUID) {
        PreconditionChecker(systematicStudyRepository, credentialsService).also {
            it.prepareIfUnauthenticatedOrUnauthorized(presenter, ResearcherId(researcherId))
        }
        if (presenter.isDone()) return

        systematicStudyRepository.findSomeByCollaborator(researcherId).let {
            presenter.prepareSuccessView(ResponseModel(researcherId, it))
        }
    }
}