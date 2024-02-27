package br.all.application.question.create.textual

import java.util.*

data class TextualDTO (
    val questionId: UUID,
    val protocolId: UUID,
    val code: String,
    val description: String
)