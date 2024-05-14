package br.all.application.question.create

import br.all.application.question.create.CreateQuestionService.QuestionType.*
import br.all.application.question.create.CreateQuestionService.RequestModel
import br.all.application.question.create.CreateQuestionService.ResponseModel
import br.all.application.question.repository.QuestionRepository
import br.all.application.question.repository.toDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.question.QuestionBuilder
import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.services.UuidGeneratorService

class CreateQuestionServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val questionRepository: QuestionRepository,
    private val uuidGeneratorService: UuidGeneratorService,
    private val credentialsService: CredentialsService,
) : CreateQuestionService {

    override fun create(
        presenter: CreateQuestionPresenter,
        request: RequestModel
    ) {
        val systematicStudyId = request.systematicStudyId
        val userId = request.userId
        val user = credentialsService.loadCredentials(userId)?.toUser()
        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if (presenter.isDone()) return

        val type = request.questionType

        if (type == PICK_LIST && request.options.isNullOrEmpty()) {
            presenter.prepareFailView(IllegalArgumentException("Options must not be null or empty."))
            return
        }
        if (type == NUMBERED_SCALE && (request.lower == null || request.higher == null)) {
            presenter.prepareFailView(IllegalArgumentException("Scale boundaries must not be null."))
            return
        }
        if (type == LABELED_SCALE && request.scales.isNullOrEmpty()) {
            presenter.prepareFailView(IllegalArgumentException("Scales must not be null or empty."))
            return
        }

        val generatedId = uuidGeneratorService.next()
        val questionId = QuestionId(generatedId)

        val builder = QuestionBuilder.with(questionId, SystematicStudyId(systematicStudyId), request.code, request.description)
        val question = when (request.questionType) {
            TEXTUAL -> builder.buildTextual()
            PICK_LIST -> builder.buildPickList(request.options!!)
            NUMBERED_SCALE -> builder.buildNumberScale(request.lower!!, request.higher!!)
            LABELED_SCALE -> builder.buildLabeledScale(request.scales!!)
        }

        questionRepository.createOrUpdate(question.toDto(type))
        presenter.prepareSuccessView(ResponseModel(userId, systematicStudyId, questionId.value))
    }
}