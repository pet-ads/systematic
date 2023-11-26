package br.all.application.question.repository

import br.all.domain.model.question.Question

interface QuestionRepository {
    fun create(question: Question<Any>)
}