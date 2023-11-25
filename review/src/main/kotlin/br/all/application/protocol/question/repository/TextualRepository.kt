package br.all.application.protocol.question.repository

import br.all.application.protocol.question.create.textual.TextualDTO
import br.all.domain.model.protocol.question.QuestionId

interface TextualRepository {
    fun create(textualDTO: TextualDTO)

    fun findById(textualId: QuestionId) : TextualDTO

    fun update(textualDTO: TextualDTO)
}