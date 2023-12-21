package br.all.application.question.create

import br.all.application.question.create.CreateQuestionService.*
import br.all.domain.model.question.QuestionId

interface CreateQuestionStrategy {
    fun create(request: RequestModel): QuestionDTO
}