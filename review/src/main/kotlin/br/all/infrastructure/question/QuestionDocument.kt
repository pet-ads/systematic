package br.all.infrastructure.question


import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudyId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("question")
data class QuestionDocument(
    @Id val questionId: UUID,
    val systematicStudyId: UUID,
    val code: String,
    val description: String,
    val questionType: String,
    val scales: Map<String, Int>? = null,
    val higher: Int? = null,
    val lower: Int? = null,
    val options: List<String>? = null,
    val context: String
)