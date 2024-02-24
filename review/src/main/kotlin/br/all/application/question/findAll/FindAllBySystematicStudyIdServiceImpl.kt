package br.all.application.question.findAll

import br.all.application.question.findAll.FindAllBySystematicStudyIdService.*
import br.all.application.question.repository.QuestionRepository
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.question.QuestionId
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId

class FindAllBySystematicStudyIdServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val questionRepository: QuestionRepository,
    private val credentialsService: ResearcherCredentialsService
): FindAllBySystematicStudyIdService {
    override fun findAllBySystematicStudyId(presenter: FindAllBySystematicStudyIdPresenter, request: RequestModel) {
        val researcherId = ResearcherId(request.researcherId)
        val systematicStudyId = SystematicStudyId(request.systematicStudyId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        if (presenter.isDone()) return

        val questions = questionRepository.findAllBySystematicStudyId(request.systematicStudyId)
        presenter.prepareSuccessView(ResponseModel(request.researcherId, request.systematicStudyId, questions))
    }
}