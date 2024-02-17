package br.all.infrastructure.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.Question
import br.all.domain.model.question.QuestionId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Update

interface MongoQuestionRepository: MongoRepository<QuestionDocument, QuestionId> {
    fun findAllById_QuestionId(questionId: ProtocolId): List<QuestionDocument>
    @Update("{ '\$set' : { ?1 : ?2 } }")
    fun findAndUpdateAttributeById(id: QuestionId, attributeName:String, newStatus: Any)
}