package br.all.infrastructure.question.factory

import br.all.application.question.create.CreateQuestionService.RequestModel
import br.all.application.question.repository.QuestionDto
import br.all.application.question.shared.QuestionFactory
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.Question
import br.all.domain.model.question.QuestionBuilder
import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudyId
import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.question.QuestionRepositoryImpl
import java.util.*
//
//class TextualQuestionFactory(private val mongoRepository: MongoQuestionRepository) : QuestionFactory {
//
//    override fun create(id: UUID, request: RequestModel) = with(request) {
//        QuestionBuilder.with(QuestionId(id), SystematicStudyId(systematicStudyId), code, description)
//            .buildTextual()
//    }
//
//    override fun toDto(question: Question<*>) = with(question) {
//        QuestionDto(id.value(), systematicStudyId.value(), code, description, "Textual")
//    }
//
//    override fun repository() = QuestionRepositoryImpl(mongoRepository)
//}