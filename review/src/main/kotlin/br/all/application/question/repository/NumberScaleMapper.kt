package br.all.application.protocol.question.repository

import br.all.application.protocol.question.create.numberScale.NumberScaleDTO
import br.all.domain.model.question.NumberScale

fun NumberScale.toDto() = NumberScaleDTO(
    questionId.value,
    protocolId.value,
    code,
    description,
    higher,
    lower
)