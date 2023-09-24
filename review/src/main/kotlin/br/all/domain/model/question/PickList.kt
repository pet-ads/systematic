package br.all.domain.model.question

import br.all.domain.model.question.Question
import br.all.domain.model.question.QuestionId

class PickList(id: QuestionId, description: String, options: List<String>) : Question(id, description) {

}