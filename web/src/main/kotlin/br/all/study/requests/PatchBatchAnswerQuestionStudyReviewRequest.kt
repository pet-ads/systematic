package br.all.study.requests

import br.all.application.study.update.interfaces.BatchAnswerQuestionService
import java.util.UUID

class PatchBatchAnswerQuestionStudyReviewRequest(
    val answers: List<AnswerPayload>
) {
    data class AnswerPayload(
        val questionId: UUID,
        val type: String,
        val answer: Any
    )

    fun toRequestModel(userId: UUID,
                       systematicStudyId: UUID,
                       studyReviewId: Long
    ): BatchAnswerQuestionService.RequestModel {
        val serviceAnswers = this.answers.map {
            BatchAnswerQuestionService.RequestModel.AnswerDetail(
                questionId = it.questionId,
                type = it.type,
                answer = it.answer
            )
        }

        return BatchAnswerQuestionService.RequestModel(
            userId = userId,
            systematicStudyId = systematicStudyId,
            studyReviewId = studyReviewId,
            answers = serviceAnswers
        )
    }
}