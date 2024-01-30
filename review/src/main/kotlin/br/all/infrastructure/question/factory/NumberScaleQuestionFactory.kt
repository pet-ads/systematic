package br.all.infrastructure.question.factory

import br.all.application.question.create.CreateQuestionService
import br.all.application.question.repository.QuestionDto
import br.all.application.question.repository.QuestionRepository
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.NumberScale
import br.all.domain.model.question.Question
import br.all.domain.model.question.QuestionBuilder
import br.all.domain.model.question.QuestionId
import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.question.QuestionRepositoryImpl
import java.util.*

class NumberScaleQuestionFactory(private val mongoRepository: MongoQuestionRepository): QuestionFactory{
    override fun create(id: UUID, request: CreateQuestionService.RequestModel) = with(request){
        if (higher == null || lower == null) throw IllegalArgumentException("Higher and Lower values must not be null.")
        QuestionBuilder.with(QuestionId(id), ProtocolId(protocolId), code, description)
            .buildNumberScale(lower, higher)
    }

    override fun toDto(question: Question<*>) = with(question as NumberScale){
        QuestionDto(id.value(), protocolId.value, code, description, "NumberScale", lower = lower, higher = higher)
    }

    override fun repository() = QuestionRepositoryImpl(mongoRepository)

}