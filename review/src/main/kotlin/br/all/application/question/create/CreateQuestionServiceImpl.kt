package br.all.application.question.create

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.question.create.CreateQuestionService.RequestModel
import br.all.application.question.create.CreateQuestionService.ResponseModel
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.services.UuidGeneratorService
import br.all.infrastructure.question.factory.QuestionFactory

class CreateQuestionServiceImpl(
    private val questionFactory: QuestionFactory,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val protocolRepository: ProtocolRepository,
    private val strategy: CreateQuestionStrategy,
    private val uuidGeneratorService: UuidGeneratorService,
    private val credentialsService: ResearcherCredentialsService,
): CreateQuestionService {
    override fun create(
        presenter: CreateQuestionPresenter,
        request: RequestModel
    ) {
        val (researcherId, systematicStudyId, protocolId) = request
        val repository = questionFactory.repository()

        PreconditionChecker(systematicStudyRepository, credentialsService).also {
            it.prepareIfViolatesPreconditions(
                presenter,
                ResearcherId(researcherId),
                SystematicStudyId(systematicStudyId),
                ProtocolId(protocolId),
                protocolRepository,
            )
        }
        if(presenter.isDone()) return

        val questionId = uuidGeneratorService.next()
        val question = questionFactory.create(questionId, request)
        strategy.addQuestion(protocolId, questionId)

        if (presenter.isDone()) return

        repository.createOrUpdate(questionFactory.toDto(question))
        presenter.prepareSuccessView(ResponseModel(researcherId, systematicStudyId, protocolId, questionId))
    }
}