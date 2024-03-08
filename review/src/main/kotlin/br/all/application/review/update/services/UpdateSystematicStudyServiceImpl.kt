package br.all.application.review.update.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.review.repository.toDto
import br.all.application.review.update.presenter.UpdateSystematicStudyPresenter
import br.all.application.review.update.services.UpdateSystematicStudyService.RequestModel
import br.all.application.review.update.services.UpdateSystematicStudyService.ResponseModel
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.SystematicStudyId
import java.util.*

class UpdateSystematicStudyServiceImpl(
    private val repository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService,
) : UpdateSystematicStudyService {
    override fun update(
        presenter: UpdateSystematicStudyPresenter,
        researcher: UUID,
        systematicStudy: UUID,
        request: RequestModel,
    ) {
        PreconditionChecker(repository, credentialsService).also {
            it.prepareIfViolatesPreconditions(presenter, ResearcherId(researcher), SystematicStudyId(systematicStudy))
        }
        if (presenter.isDone()) return

        val dto = repository.findById(systematicStudy)
        val updated = dto?.let {
            SystematicStudy.fromDto(it).apply {
                title = request.title ?: title
                description = request.description ?: description
            }.toDto()
        }

        if (updated != null && updated != dto) repository.saveOrUpdate(updated)
        presenter.prepareSuccessView(ResponseModel(researcher, systematicStudy))
    }
}
