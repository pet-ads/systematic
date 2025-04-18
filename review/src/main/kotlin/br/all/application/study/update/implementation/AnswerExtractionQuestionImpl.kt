package br.all.application.study.update.implementation

import br.all.application.question.repository.QuestionRepository
import br.all.application.question.repository.fromDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.fromDto
import br.all.application.study.repository.toDto
import br.all.application.study.update.interfaces.AnswerExtractionQuestionPresenter
import br.all.application.study.update.interfaces.AnswerExtractionQuestionService
import br.all.application.user.CredentialsService
import br.all.domain.model.question.*
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.Answer
import br.all.domain.model.study.StudyReview

class AnswerExtractionQuestionImpl(
    private val studyReviewRepository: StudyReviewRepository,
    private val questionRepository: QuestionRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
): AnswerExtractionQuestionService {
    override fun answerExtractionQuestion(
        presenter: AnswerExtractionQuestionPresenter,
        request: AnswerExtractionQuestionService.RequestModel<*>
    ) {

        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if (presenter.isDone()) return

        val (userId, systematicStudyId, studyReviewId, questionId) = request

        val reviewDto = studyReviewRepository.findById(systematicStudyId, studyReviewId)
        if (reviewDto == null) {
            val message = "Review with id $studyReviewId in systematic study $systematicStudyId does not exist!"
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        val review = StudyReview.fromDto(reviewDto)

        val questionDto = questionRepository.findById(systematicStudyId, questionId)
        if (questionDto == null) {
            val message = "There is not a question with id $questionId defined in protocol $systematicStudyId"
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        val question = Question.fromDto(questionDto)


        val answer = answer(questionDto.questionType, request, question)
        review.answerFormQuestionOf(answer)

        studyReviewRepository.saveOrUpdate(review.toDto())

        presenter.prepareSuccessView(
            AnswerExtractionQuestionService.ResponseModel(
                userId,
                systematicStudyId,
                studyReviewId
            )
        )
    }

    private fun answer(
        type: String,
        request: AnswerExtractionQuestionService.RequestModel<*>,
        question: Question<*>
    ): Answer<*> {
        if (type != request.type) {
            val message = "Answer for ${request.type} has been sent, but question ${question.id} is actually $type"
            throw IllegalArgumentException(message)
        }
        return when {
            type == "TEXTUAL" && request.answer is String -> (question as Textual).answer(request.answer)
            type == "PICK_LIST" && request.answer is String -> (question as PickList).answer(request.answer)
            type == "NUMBERED_SCALE" && request.answer is Int -> (question as NumberScale).answer(request.answer)
            type == "LABELED_SCALE" -> {
                when (val answer = request.answer) {
                    is LinkedHashMap<*, *> -> {
                        (answer["name"] as? String)?.let { name ->
                            (answer["value"] as? Int)?.let { value ->
                                (question as LabeledScale).answer(Label(name, value))
                            }
                        } ?: throw IllegalArgumentException("Invalid labeled scale answer: missing 'name' or 'value'")
                    }
                    is AnswerExtractionQuestionService.LabelDto -> {
                        (question as LabeledScale).answer(Label(answer.name, answer.value))
                    }
                    else -> {
                        throw IllegalArgumentException("Unsupported answer type for LABELED_SCALE")
                    }
                }
            }
            else -> {
                val message = "Answer type of ${request.answer?.javaClass} is not compatible with question type $type"
                throw IllegalArgumentException(message)
            }
        }
    }
}