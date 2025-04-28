package br.all.study.requests

import br.all.application.study.update.interfaces.AnswerQuestionService
import java.util.*

class PatchAnswerQuestionStudyReviewRequest<T>(
    val questionId: UUID,
    val type: String,
    val answer: T
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID, studyReviewId: Long)
            = AnswerQuestionService.RequestModel(
        userId,
        systematicStudyId,
        studyReviewId,
        questionId,
        type,
        answer
    )
}