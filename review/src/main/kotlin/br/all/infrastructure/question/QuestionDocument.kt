package br.all.infrastructure.question


import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudyId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("question")
data class QuestionDocument(
    @Id val id: UUID,
    val protocolId: UUID,
    val code: String,
    val description: String,
    val questionType: String,
    val scales: Map<String, Int>?,
    val higher: Int?,
    val lower: Int?,
    val options: List<String>?
){
    companion object{
        @Transient
        val SEQUENCE_NAME = "question_sequence"
    }
}