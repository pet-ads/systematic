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
        PreconditionChecker(systematicStudyRepository, credentialsService).also {
            it.prepareIfUnauthenticatedOrUnauthorized(presenter, ResearcherId(researcherId))
        }
        if (presenter.isDone()) return

        systematicStudyRepository.findSomeByCollaborator(researcherId).let {
            presenter.prepareSuccessView(ResponseModel(researcherId, it))
        }
    }
}