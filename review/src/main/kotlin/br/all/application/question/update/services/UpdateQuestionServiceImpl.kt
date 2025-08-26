package br.all.application.question.update.services

import br.all.application.question.create.CreateQuestionService.QuestionType
import br.all.application.question.create.CreateQuestionService.QuestionType.PICK_LIST
import br.all.application.question.create.CreateQuestionService.QuestionType.PICK_MANY
import br.all.application.question.repository.QuestionRepository
import br.all.application.question.repository.toDto
import br.all.application.question.update.presenter.UpdateQuestionPresenter
import br.all.application.question.update.services.UpdateQuestionService.*
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.question.QuestionBuilder
import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.SystematicStudyId

class UpdateQuestionServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val questionRepository: QuestionRepository,
    private val credentialsService: CredentialsService,
) : UpdateQuestionService {
    override fun update(presenter: UpdateQuestionPresenter, request: RequestModel) {
        val systematicStudyId = request.systematicStudyId
        val userId = request.userId
        val user = credentialsService.loadCredentials(userId)?.toUser()
        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if (presenter.isDone()) return

        val type = request.questionType

        if ((type == PICK_LIST || type == PICK_MANY) && request.options.isNullOrEmpty()) {
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

        val builder = QuestionBuilder.with(questionId, SystematicStudyId(systematicStudyId), request.code, request.description)

        try {
            val question = when (request.questionType) {
                QuestionType.TEXTUAL -> builder.buildTextual()
                QuestionType.PICK_LIST -> builder.buildPickList(request.options!!)
                QuestionType.PICK_MANY -> builder.buildPickMany(request.options!!)
                QuestionType.NUMBERED_SCALE -> builder.buildNumberScale(request.lower!!, request.higher!!)
                QuestionType.LABELED_SCALE -> builder.buildLabeledScale(request.scales!!)
            }
            questionRepository.createOrUpdate(question.toDto(type, request.questionContext))
        } catch (e: IllegalArgumentException) {
            presenter.prepareFailView(e)
            return
        }

        presenter.prepareSuccessView(ResponseModel(userId, systematicStudyId, questionId.value))
    }
}
