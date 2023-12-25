package br.all.application.question.find

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.question.find.FindQuestionService.*
import br.all.application.question.repository.QuestionRepository
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId

class FindQuestionServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val questionRepository: QuestionRepository,
    private val credentialsService: ResearcherCredentialsService
) : FindQuestionService {

    override fun findOne(presenter: FindQuestionPresenter, request: RequestModel) {
        val researcherId = ResearcherId(request.researcherId)
        val systematicStudyId = SystematicStudyId(request.systematicStudyId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        if (presenter.isDone()) return

        val question = questionRepository.findById(request.systematicStudyId, request.questionId)
        if (question === null) {
            val message =
                "There is no review of id ${request.systematicStudyId} or question of id ${request.questionId}."
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }
        presenter.prepareSuccessView(FindQuestionService.ResponseModel(request.researcherId, question))
    }
}
