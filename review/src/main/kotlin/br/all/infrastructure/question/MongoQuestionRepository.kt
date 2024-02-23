package br.all.infrastructure.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.Question
import br.all.domain.model.question.QuestionId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Update
import java.util.UUID

interface MongoQuestionRepository: MongoRepository<QuestionDocument, UUID> {
//    fun findAllById_QuestionId(questionId: ProtocolId): List<QuestionDocument>
//    @Update("{ '\$set' : { ?1 : ?2 } }")
//    fun findAndUpdateAttributeById(id: QuestionId, attributeName:String, newStatus: Any)
}