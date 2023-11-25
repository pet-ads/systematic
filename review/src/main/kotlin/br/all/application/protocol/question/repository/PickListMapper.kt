package br.all.application.protocol.question.repository

import br.all.application.protocol.question.create.pickList.PickListDTO
import br.all.domain.model.protocol.question.PickList

fun PickList.toDto() = PickListDTO(
    questionId.value,
    protocolId.value,
    code,
    description,
    options
)