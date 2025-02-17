package br.all.question.requests

import br.all.application.question.create.CreateQuestionService
import br.all.application.question.update.services.UpdateQuestionService
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(name = "UpdateQuestionPutRequest")
data class PutRequest(
    val questionType: CreateQuestionService.QuestionType,
    val code: String,
    val description: String,
    val scales: Map<String, Int>? = null,
    val higher: Int? = null,
    val lower: Int? = null,
    val options: List<String>? = null,
    val context: String? = null
) {
    fun toUpdateRequestModel(userId: UUID, systematicStudyId: UUID, questionId: UUID) =
        UpdateQuestionService.RequestModel(
            userId,
            systematicStudyId,
            questionId,
            questionType,
            context,
            code,
            description,
            scales,
            higher,
            lower,
            options
        )
}
