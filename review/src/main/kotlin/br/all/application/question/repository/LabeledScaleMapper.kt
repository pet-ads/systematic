package br.all.application.question.repository

import br.all.application.question.create.labeledScale.LabeledScaledDTO
import br.all.domain.model.question.LabeledScale

fun LabeledScale.toDto() = LabeledScaledDTO(
    id.value(),
    protocolId.value,
    code,
    description,
    getScales()
)