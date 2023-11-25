package br.all.application.protocol.question.repository

import br.all.domain.model.protocol.question.Question

interface QuestionRepository {
    fun create(question: Question<*>)
}