package br.all.application.question.repository

import br.all.application.protocol.question.create.numberScale.NumberScaleDTO
import br.all.domain.model.question.NumberScale

fun NumberScale.toDto() = NumberScaleDTO(
    id.value(),
    protocolId.value,
    code,
    description,
    higher,
    lower
)