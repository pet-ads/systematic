package br.all.application.protocol.question.repository

import br.all.application.protocol.question.create.labeledScale.LabeledScaledDTO
import br.all.domain.model.protocol.question.LabeledScale

fun LabeledScale.toDto() = LabeledScaledDTO(
    questionId.value,
    protocolId.value,
    code,
    description,
    getScales()
)