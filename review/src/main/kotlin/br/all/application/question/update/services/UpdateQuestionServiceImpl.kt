package br.all.application.question.update.services

import br.all.application.question.create.CreateQuestionService.QuestionType
import br.all.application.question.repository.QuestionRepository
import br.all.application.question.repository.toDto
import br.all.application.question.update.presenter.UpdateQuestionPresenter
import br.all.application.question.update.services.UpdateQuestionService.*
import br.all.application.user.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.domain.model.question.QuestionBuilder
import br.all.domain.model.question.QuestionId
import br.all.domain.model.user.ResearcherId
import br.all.domain.model.review.SystematicStudyId

class UpdateQuestionServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val questionRepository: QuestionRepository,
    private val credentialsService: ResearcherCredentialsService,
) : UpdateQuestionService {
    override fun update(presenter: UpdateQuestionPresenter, request: RequestModel) {
        val researcherId = ResearcherId(request.researcherId)
        val systematicStudyId = SystematicStudyId(request.systematicStudyId)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

        if (presenter.isDone()) return

        val type = request.questionType

        if (type == QuestionType.PICK_LIST && request.options.isNullOrEmpty()) {
            presenter.prepareFailView(IllegalArgumentException("Options must not be null or empty."))
            return
        }
        if (type == QuestionType.NUMBERED_SCALE && (request.lower == null || request.higher == null)) {
            presenter.prepareFailView(IllegalArgumentException("Scale boundaries must not be null."))
            return
        }
        if (type == QuestionType.LABELED_SCALE && request.scales.isNullOrEmpty()) {
            presenter.prepareFailView(IllegalArgumentException("Scales must not be null or empty."))
            return
        }

        val questionId = QuestionId(request.questionId)

        val builder = QuestionBuilder.with(questionId, systematicStudyId, request.code, request.description)
        val question = when (request.questionType) {
            QuestionType.TEXTUAL -> builder.buildTextual()
            QuestionType.PICK_LIST -> builder.buildPickList(request.options!!)
            QuestionType.NUMBERED_SCALE -> builder.buildNumberScale(request.lower!!, request.higher!!)
            QuestionType.LABELED_SCALE -> builder.buildLabeledScale(request.scales!!)
        }

        questionRepository.createOrUpdate(question.toDto(type))
        presenter.prepareSuccessView(ResponseModel(researcherId.value, systematicStudyId.value(), questionId.value))
    }

}