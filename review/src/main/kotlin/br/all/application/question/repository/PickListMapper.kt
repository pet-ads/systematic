package br.all.application.question.repository

import br.all.application.question.create.pickList.PickListDTO
import br.all.domain.model.question.PickList

fun PickList.toDto() = PickListDTO(
    id.value(),
    protocolId.value,
    code,
    description,
    options
)