package br.all.application.protocol.question.repository

import br.all.domain.model.question.Question

interface QuestionRepository {
    fun create(question: Question<*>)
}