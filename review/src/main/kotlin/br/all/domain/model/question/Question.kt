package br.all.domain.model.question

import br.all.domain.shared.ddd.Entity

abstract class Question(
    id: QuestionId,
    val description: String
): Entity(id) {

}