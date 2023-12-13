package br.all.application.review.create

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.create.CreateSystematicStudyService.RequestModel
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudy
import br.all.domain.services.UuidGeneratorService
import java.util.*

class CreateSystematicStudyServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val uuidGeneratorService: UuidGeneratorService,
    private val credentialsService: ResearcherCredentialsService,
): CreateSystematicStudyService {
    override fun create(presenter: CreateSystematicStudyPresenter, researcherId: UUID, request: RequestModel) {
        val ownerId = ResearcherId(researcherId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfUnauthenticatedOrUnauthorized(presenter, ownerId)

        if (presenter.isDone()) return

        try {
            val id = uuidGeneratorService.next()
            val systematicStudy = SystematicStudy.fromRequestModel(id, researcherId, request)
            systematicStudyRepository.saveOrUpdate(systematicStudy.toDto())

            presenter.prepareSuccessView(ResponseModel(researcherId, id))
        }
        catch (e: Throwable) {
            presenter.prepareFailView(e)
        }
    }
}
