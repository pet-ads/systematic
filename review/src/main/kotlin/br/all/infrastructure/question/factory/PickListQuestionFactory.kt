package br.all.infrastructure.question.factory

import br.all.application.question.create.CreateQuestionService.RequestModel
import br.all.application.question.repository.QuestionDto
import br.all.application.question.shared.QuestionFactory
import br.all.domain.model.question.PickList
import br.all.domain.model.question.Question
import br.all.domain.model.question.QuestionBuilder
import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudyId
import br.all.infrastructure.question.MongoQuestionRepository
import br.all.infrastructure.question.QuestionRepositoryImpl
import java.util.*

class PickListQuestionFactory(private val mongoRepository: MongoQuestionRepository) : QuestionFactory {
    override fun create(id: UUID, request: RequestModel) = with(request) {
        if (options == null) throw IllegalArgumentException("Options list must not be null.")
        QuestionBuilder.with(QuestionId(id), SystematicStudyId(systematicStudyId), code, description)
            .buildPickList(options)
    }

    override fun toDto(question: Question<*>) = with(question as PickList) {
        QuestionDto(id.value(), systematicStudyId.value(), code, description, "PickList", options = options)
    }

    override fun repository() = QuestionRepositoryImpl(mongoRepository)

}