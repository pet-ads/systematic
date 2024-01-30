package br.all.application.question.create

import java.util.*

sealed interface CreateQuestionStrategy {
    fun addQuestion(protocolId: UUID, questionId: UUID)
}
