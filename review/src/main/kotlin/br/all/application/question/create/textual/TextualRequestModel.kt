package br.all.application.question.create.textual

import br.all.domain.model.protocol.ProtocolId

data class TextualRequestModel(
    val protocolId: ProtocolId,
    val code: String,
    val description: String
)