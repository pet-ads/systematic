package br.all.application.question.create.pickList

import br.all.application.question.create.CreateQuestionService.*
import br.all.application.question.create.CreateQuestionStrategy
import br.all.application.question.create.QuestionDTO
import br.all.application.question.repository.toDto
import br.all.domain.model.question.QuestionBuilder
import br.all.domain.model.question.QuestionId

class CreatePickListQuestionStrategy() : CreateQuestionStrategy {
    override fun create(request: RequestModel): QuestionDTO {
        val questionId = QuestionId(request.questionId.value)
        val pickList = QuestionBuilder()
            .with(questionId, request.protocolId, request.code, request.description)
            .buildPickList(request.options)

        return pickList.toDto()
    }
}