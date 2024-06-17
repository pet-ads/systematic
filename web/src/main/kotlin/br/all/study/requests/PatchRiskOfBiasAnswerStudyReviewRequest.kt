package br.all.study.requests

import br.all.application.study.update.interfaces.AnswerRiskOfBiasQuestionService
import java.util.*

data class PatchRiskOfBiasAnswerStudyReviewRequest<T>(
    val questionId: UUID,
    val type: String,
    val answer: T
) {
    fun toRequestModel(userId: UUID, systematicStudyId: UUID, studyReviewId: Long)
    = AnswerRiskOfBiasQuestionService.RequestModel(
        userId,
        systematicStudyId,
        studyReviewId,
        questionId,
        type,
        answer
    )
}