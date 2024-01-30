package br.all.infrastructure.question.factory

import br.all.application.question.create.CreateQuestionService.RequestModel
import br.all.application.question.repository.QuestionDto
import br.all.application.question.repository.QuestionRepository
import br.all.domain.model.question.Question
import java.util.*

interface QuestionFactory {
    fun create(id: UUID, request: RequestModel): Question<*>

    fun toDto(question: Question<*>): QuestionDto

    fun repository(): QuestionRepository
}
