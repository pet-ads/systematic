package br.all.application.review.find.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.find.presenter.FindSystematicStudyPresenter
import br.all.application.review.find.services.FindSystematicStudyService.RequestModel
import br.all.application.review.find.services.FindSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.researcher.toResearcherId
import br.all.domain.model.review.toSystematicStudyId

class FindSystematicStudyServiceImpl(
    private val repository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService,
) : FindSystematicStudyService {
    override fun findById(presenter: FindSystematicStudyPresenter, request: RequestModel) {
        val (researcher, systematicStudy) = request
        PreconditionChecker(repository, credentialsService).also {
            it.prepareIfViolatesPreconditions(presenter, researcher.toResearcherId(), systematicStudy.toSystematicStudyId())
        }
        if (presenter.isDone()) return

        repository.findById(systematicStudy)?.let {
            presenter.prepareSuccessView(ResponseModel(researcher, systematicStudy, it))
        }
    }
}
