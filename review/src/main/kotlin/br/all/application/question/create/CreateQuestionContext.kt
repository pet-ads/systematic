package br.all.application.question.create

import br.all.infrastructure.question.factory.QuestionFactory

class CreateQuestionContext(questionType: String) {
    private fun setStrategy(questionType: String): QuestionFactory {
        return when (questionType) {
//            "LabeledScale" -> CreateLabeledScaleQuestionStrategy()
//            "NumberScale" -> CreateNumberScaleQuestionStrategy()
//            "PickList" -> CreatePickListQuestionStrategy()
//            "Textual" -> CreateTextualQuestionStrategy()
            else -> throw IllegalArgumentException("Invalid Question Type: $questionType")
        }
    }
}