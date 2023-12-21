package br.all.application.question.create.numberScale

import br.all.application.question.create.CreateQuestionService
import br.all.application.question.create.CreateQuestionService.*
import br.all.application.question.create.CreateQuestionStrategy
import br.all.application.question.create.QuestionDTO
import br.all.application.question.repository.toDto
import br.all.domain.model.question.QuestionBuilder
import br.all.domain.model.question.QuestionId

class CreateNumberScaleQuestionStrategy() : CreateQuestionStrategy {
    override fun create(request: RequestModel): QuestionDTO {
        val questionId = QuestionId(request.questionId.value)
        val numberScale = QuestionBuilder()
            .with(questionId, request.protocolId, request.code, request.description)
            .buildNumberScale(request.lower, request.higher)

        return numberScale.toDto()
    }
}