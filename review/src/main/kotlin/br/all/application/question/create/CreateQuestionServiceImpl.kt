package br.all.application.question.create

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.question.create.CreateQuestionService.RequestModel
import br.all.application.question.create.CreateQuestionService.ResponseModel
import br.all.application.question.repository.QuestionRepository
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId

class CreateQuestionServiceImpl(
    private val questionRepository: QuestionRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val protocolRepository: ProtocolRepository,
    private val credentialsService: ResearcherCredentialsService
): CreateQuestionService {
    override fun createRiskOfBiasQuestion(
        presenter: CreateQuestionPresenter,
        request: RequestModel
    ) {
        val researcherId = ResearcherId(request.researcherId)
        val systematicStudyId = SystematicStudyId(request.systematicStudyId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        if(presenter.isDone()) return

        val context = CreateQuestionContext(request.questionType)
        val dto = context.executeStrategy(request)

        protocolRepository.addRiskOfBiasQuestion(request.questionId)
        questionRepository.createOrUpdate(dto)

        presenter.prepareSuccessView(ResponseModel(request.researcherId, request.systematicStudyId, request.protocolId, request.questionId))
    }

    override fun createExtractionQuestion(
        presenter: CreateQuestionPresenter,
        request: RequestModel
    ) {
        val researcherId = ResearcherId(request.researcherId)
        val systematicStudyId = SystematicStudyId(request.systematicStudyId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        if(presenter.isDone()) return

        val context = CreateQuestionContext(request.questionType)
        val dto = context.executeStrategy(request)

        protocolRepository.addExtractionQuestion(request.questionId)
        questionRepository.createOrUpdate(dto)

        presenter.prepareSuccessView(ResponseModel(request.researcherId, request.systematicStudyId, request.protocolId, request.questionId))
    }
}