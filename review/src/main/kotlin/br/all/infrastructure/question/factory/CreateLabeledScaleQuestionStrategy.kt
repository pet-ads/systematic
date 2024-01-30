package br.all.infrastructure.question.factory
//
//import br.all.application.question.create.CreateQuestionService.*
//import br.all.application.question.create.CreateQuestionStrategy
//import br.all.application.question.create.QuestionDTO
//import br.all.domain.model.question.QuestionBuilder
//import br.all.domain.model.question.QuestionId
//
//class CreateLabeledScaleQuestionStrategy() : CreateQuestionStrategy {
//    override fun create(request: RequestModel): QuestionDTO {
//        val questionId = QuestionId(request.questionId.value)
//        val labeledScale = QuestionBuilder.with(questionId, request.protocolId, request.code, request.description)
//            .buildLabeledScale(request.scales)
//        return labeledScale.toDto()
//    }
//}