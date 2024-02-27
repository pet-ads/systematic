package br.all.application.question.repository

import br.all.application.protocol.question.create.numberScale.NumberScaleDTO
import br.all.application.question.create.textual.TextualDTO
import br.all.application.question.create.labeledScale.LabeledScaledDTO
import br.all.application.question.create.pickList.PickListDTO
import br.all.domain.model.question.LabeledScale
import br.all.domain.model.question.NumberScale
import br.all.domain.model.question.PickList
import br.all.domain.model.question.Textual

fun LabeledScale.toDto() = LabeledScaledDTO(
    id.value(),
    protocolId.value,
    code,
    description,
    getScales()
)

fun NumberScale.toDto() = NumberScaleDTO(
    id.value(),
    protocolId.value,
    code,
    description,
    higher,
    lower
)

fun Textual.toDto() = TextualDTO(
    id.value(),
    protocolId.value,
    code,
    description
)

fun PickList.toDto() = PickListDTO(
    id.value(),
    protocolId.value,
    code,
    description,
    options
)