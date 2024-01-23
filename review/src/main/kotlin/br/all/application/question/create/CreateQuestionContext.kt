package br.all.application.question.create

import br.all.application.question.create.CreateQuestionService.RequestModel

class CreateQuestionContext(questionType: String) {
    private val strategy: CreateQuestionStrategy = setStrategy(questionType)
    private fun setStrategy(questionType: String): CreateQuestionStrategy {
        return when (questionType) {
//            "LabeledScale" -> CreateLabeledScaleQuestionStrategy()
//            "NumberScale" -> CreateNumberScaleQuestionStrategy()
//            "PickList" -> CreatePickListQuestionStrategy()
//            "Textual" -> CreateTextualQuestionStrategy()
            else -> throw IllegalArgumentException("Invalid Question Type: $questionType")
        }
    }

    fun executeStrategy(request: RequestModel) = strategy.create(request)
}