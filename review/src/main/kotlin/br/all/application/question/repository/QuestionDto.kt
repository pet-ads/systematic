package br.all.application.question.repository

import br.all.domain.model.question.QuestionId

data class QuestionDto(
    val id: QuestionId,
    val description: String
) {
}