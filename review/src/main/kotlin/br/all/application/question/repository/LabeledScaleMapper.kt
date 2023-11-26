package br.all.application.protocol.question.repository

import br.all.application.question.create.labeledScale.LabeledScaledDTO
import br.all.domain.model.question.LabeledScale

fun LabeledScale.toDto() = LabeledScaledDTO(
    questionId.value,
    protocolId.value,
    code,
    description,
    getScales()
)