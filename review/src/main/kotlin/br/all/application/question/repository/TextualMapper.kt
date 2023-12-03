package br.all.application.question.repository

import br.all.application.protocol.question.create.numberScale.NumberScaleDTO
import br.all.application.protocol.question.create.textual.TextualDTO
import br.all.domain.model.question.Textual


fun Textual.toDto() = TextualDTO(
    id.value(),
    protocolId.value,
    code,
    description
)