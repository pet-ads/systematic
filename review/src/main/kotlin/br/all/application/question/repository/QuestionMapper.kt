package br.all.application.question.repository

import br.all.application.question.create.QuestionDTO
import br.all.domain.model.question.LabeledScale
import br.all.domain.model.question.NumberScale
import br.all.domain.model.question.PickList
import br.all.domain.model.question.Textual

fun LabeledScale.toDto() = QuestionDTO(
    id.value(),
    protocolId.value,
    code,
    description,
    "LabeledScale",
    getScales(),
    null,
    null,
    null
)

fun NumberScale.toDto() = QuestionDTO(
    id.value(),
    protocolId.value,
    code,
    description,
    "NumberScale",
    null,
    higher,
    lower,
    null
)

fun Textual.toDto() = QuestionDTO(
    id.value(),
    protocolId.value,
    code,
    description,
    "Textual",
    null,
    null,
    null,
    null
)

fun PickList.toDto() = QuestionDTO(
    id.value(),
    protocolId.value,
    code,
    description,
    "PickList",
    null,
    null,
    null,
    options
)