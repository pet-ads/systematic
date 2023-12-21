package br.all.application.question.create

import br.all.application.question.create.CreateQuestionService.*
import br.all.application.question.create.labeledScale.CreateLabeledScaleQuestionStrategy
import br.all.application.question.create.numberScale.CreateNumberScaleQuestionStrategy
import br.all.application.question.create.pickList.CreatePickListQuestionStrategy
import br.all.application.question.create.textual.CreateTextualQuestionStrategy

class CreateQuestionContext(questionType: String) {
    private val strategy: CreateQuestionStrategy = setStrategy(questionType)
    private fun setStrategy(questionType: String): CreateQuestionStrategy {
        return when (questionType) {
            "LabeledScale" -> CreateLabeledScaleQuestionStrategy()
            "NumberScale" -> CreateNumberScaleQuestionStrategy()
            "PickList" -> CreatePickListQuestionStrategy()
            "Textual" -> CreateTextualQuestionStrategy()
            else -> throw IllegalArgumentException("Invalid Question Type: $questionType")
        }
    }

    fun executeStrategy(request: RequestModel) = strategy.create(request)
}