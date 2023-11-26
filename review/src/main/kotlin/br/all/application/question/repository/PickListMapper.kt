package br.all.application.protocol.question.repository

import br.all.application.question.create.pickList.PickListDTO
import br.all.domain.model.question.PickList

fun PickList.toDto() = PickListDTO(
    questionId.value,
    protocolId.value,
    code,
    description,
    options
)