package br.all.application.protocol.question.create.pickList

import java.util.*

data class PickListDTO(
    val questionId: UUID,
    val protocolId: UUID,
    val code: String,
    val description: String,
    val options: List<String>
)