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
import br.all.application.study.update.interfaces.BatchAnswerQuestionPresenter
import br.all.application.study.update.interfaces.BatchAnswerQuestionService
import br.all.application.study.update.interfaces.BatchAnswerQuestionService.RequestModel
import br.all.application.study.update.interfaces.BatchAnswerQuestionService.RequestModel.AnswerDetail
import br.all.application.study.update.interfaces.BatchAnswerQuestionService.ResponseModel
import br.all.application.study.update.interfaces.BatchAnswerQuestionService.FailedAnswer
import br.all.application.study.update.interfaces.BatchAnswerQuestionService.LabelDto
import br.all.application.user.CredentialsService
import br.all.domain.model.question.Label
import br.all.domain.model.question.LabeledScale
import br.all.domain.model.question.NumberScale
import br.all.domain.model.question.PickList
import br.all.domain.model.question.Question
import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.model.question.Textual
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.Answer
import br.all.domain.model.study.StudyReview
import java.util.UUID

class BatchAnswerQuestionServiceImpl(
    private val studyReviewRepository: StudyReviewRepository,
    private val questionRepository: QuestionRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: CredentialsService,
): BatchAnswerQuestionService {
    override fun batchAnswerQuestion(
        presenter: BatchAnswerQuestionPresenter,
        request: RequestModel,
        context: String
    ) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        val reviewDto = studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId)
        if (reviewDto == null) {
            val message = "Review with id ${request.studyReviewId} in systematic study ${request.systematicStudyId} does not exist!"
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }
        val review = StudyReview.fromDto(reviewDto)

        val questionContext = QuestionContextEnum.valueOf(context.uppercase())
        val successfulQuestionIds = mutableListOf<UUID>()
        val failedAnswers = mutableListOf<FailedAnswer>()
        var totalAnswered = 0

        for (answerDetail in request.answers) {
            try {
                val questionDto = questionRepository.findById(request.systematicStudyId, answerDetail.questionId)

                if (questionDto == null) throw EntityNotFoundException("Question with id ${answerDetail.questionId} in systematic study ${request.systematicStudyId} was not found!")
                if (questionDto.context != questionContext) throw IllegalArgumentException("Should answer question with the context: $context, found: ${questionDto.context}")

                val question = Question.fromDto(questionDto)
                val answer = convertAnswer(question, answerDetail, questionDto.questionType)

                if (questionDto.context == QuestionContextEnum.ROB) {
                    review.answerQualityQuestionOf(answer)
                } else {
                    review.answerFormQuestionOf(answer)
                }

                successfulQuestionIds.add(answerDetail.questionId)
                totalAnswered++
            } catch (e: Exception) {
                failedAnswers.add(
                    FailedAnswer(
                        questionId = answerDetail.questionId,
                        reason = e.message ?: "An unknown error occurred!"
                    )
                )
            }
        }

        studyReviewRepository.saveOrUpdate(review.toDto())

        presenter.prepareSuccessView(
            ResponseModel(
                userId = request.userId,
                systematicStudyId = request.systematicStudyId,
                studyReviewId = request.studyReviewId,
                succeededAnswers = successfulQuestionIds,
                failedAnswers = failedAnswers,
                totalAnswered = totalAnswered
            )
        )
    }

    private fun convertAnswer(
        question: Question<*>,
        detail: AnswerDetail,
        questionType: String
    ): Answer<*> {
        if (detail.type != questionType) {
            throw IllegalArgumentException("Type mismatch: Request payload type is '${detail.type}', but question ${question.id} is of type '${questionType}'")
        }
        return when {
            questionType == "TEXTUAL" && detail.answer is String -> (question as Textual).answer(detail.answer)
            questionType == "PICK_LIST" && detail.answer is String -> (question as PickList).answer(detail.answer)
            questionType == "NUMBERED_SCALE" && detail.answer is Int -> (question as NumberScale).answer(detail.answer)
            questionType == "LABELED_SCALE" -> {
                when (val answer = detail.answer) {
                    is LinkedHashMap<*, *> -> {
                        (answer["name"] as? String)?.let { name ->
                            (answer["value"] as? Int)?.let { value ->
                                (question as LabeledScale).answer(Label(name, value))
                            }
                        } ?: throw IllegalArgumentException("Invalid labeled scale answer: missing 'name' or 'value'")
                    }
                    is LabelDto -> {
                        (question as LabeledScale).answer(Label(answer.name, answer.value))
                    }
                    else -> {
                        throw IllegalArgumentException("Unsupported answer type for 'LABELED_SCALE'")
                    }
                }
            }
            else -> throw IllegalArgumentException("Answer type of '${detail.answer.javaClass}' is not compatible with question type '${questionType}'")
        }
    }
}