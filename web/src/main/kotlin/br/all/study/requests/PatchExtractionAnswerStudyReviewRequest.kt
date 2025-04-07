package br.all.study.requests

import br.all.application.study.update.interfaces.AnswerExtractionQuestionService
import java.util.*

class PatchExtractionAnswerStudyReviewRequest<T>(
    val questionId: UUID,
    val type: String,
    val answer: T
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID, studyReviewId: Long)
            = AnswerExtractionQuestionService.RequestModel(
        userId,
        systematicStudyId,
        studyReviewId,
        questionId,
        type,
        answer
    )
}