package br.all.application.question.create.numberScale

import br.all.domain.model.protocol.ProtocolId

data class NumberScaleRequestModel(
    val protocolId: ProtocolId,
    val code: String,
    val description: String,
    val higher: Int,
    val lower: Int
)
