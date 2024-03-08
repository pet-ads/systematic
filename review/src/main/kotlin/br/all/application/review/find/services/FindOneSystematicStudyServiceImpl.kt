package br.all.application.review.find.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.find.presenter.FindOneSystematicStudyPresenter
import br.all.application.review.find.services.FindOneSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import java.util.*

class FindOneSystematicStudyServiceImpl(
    private val repository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService,
) : FindOneSystematicStudyService {
    override fun findById(presenter: FindOneSystematicStudyPresenter, researcher: UUID, systematicStudy: UUID) {
        PreconditionChecker(repository, credentialsService).also {
            it.prepareIfViolatesPreconditions(presenter, ResearcherId(researcher), SystematicStudyId(systematicStudy))
        }
        if (presenter.isDone()) return

        repository.findById(systematicStudy)?.let {
            presenter.prepareSuccessView(ResponseModel(researcher, systematicStudy, it))
        }
    }
}
