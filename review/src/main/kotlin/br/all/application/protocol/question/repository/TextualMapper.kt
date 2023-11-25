package br.all.application.protocol.question.repository

import br.all.application.protocol.question.create.numberScale.NumberScaleDTO
import br.all.application.protocol.question.create.textual.TextualDTO
import br.all.domain.model.protocol.question.Textual


fun Textual.toDto() = TextualDTO(
    questionId.value,
    protocolId.value,
    code,
    description
)