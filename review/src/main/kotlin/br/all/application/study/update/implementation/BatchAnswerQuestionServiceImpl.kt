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
import br.all.application.study.update.interfaces.BatchAnswerQuestionService.ResponseModel
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
        var answersProcessed = 0

        for (answerDetail in request.answers) {
            val questionDto = questionRepository.findById(request.systematicStudyId, answerDetail.questionId)

            if (questionDto == null) continue
            if (QuestionContextEnum.valueOf(context) != questionDto.context) continue

            try {
                val question = Question.fromDto(questionDto)
                val answer = convertAnswer(answerDetail.type, answerDetail.answer, question)

                if (questionDto.context == QuestionContextEnum.ROB) {
                    review.answerQualityQuestionOf(answer)
                } else {
                    review.answerFormQuestionOf(answer)
                }

                answersProcessed++
            } catch (e: Exception) {
                continue
            }
        }

        studyReviewRepository.saveOrUpdate(review.toDto())

        presenter.prepareSuccessView(
            ResponseModel(
                request.userId,
                request.systematicStudyId,
                request.studyReviewId,
                answersProcessed
            )
        )
    }

    private fun convertAnswer(
        type: String,
        rawAnswer: Any,
        question: Question<*>
    ): Answer<*> {
        return when {
            type == "TEXTUAL" && rawAnswer is String -> (question as Textual).answer(rawAnswer)
            type == "PICK_LIST" && rawAnswer is String -> (question as PickList).answer(rawAnswer)
            type == "NUMBERED_SCALE" && rawAnswer is Int -> (question as NumberScale).answer(rawAnswer)
            type == "LABELED_SCALE" -> {
                when (rawAnswer) {
                    is Map<*, *> -> {
                        val name = rawAnswer["name"] as? String
                        val value = rawAnswer["value"] as? Int
                        if (name != null && value != null) {
                            (question as LabeledScale).answer(Label(name, value))
                        } else throw IllegalArgumentException("Invalid labeled scale answer")
                    }
                    else -> throw IllegalArgumentException("Unsupported LABELED_SCALE format")
                }
            }
            else -> throw IllegalArgumentException("Unsupported answer type or mismatched value")
        }
    }
}