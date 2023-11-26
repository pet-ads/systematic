package br.all.application.question.create.labeledScale

import br.all.domain.model.protocol.ProtocolId

data class LabeledScaleRequestModel(
    val protocolId: ProtocolId,
    val code: String,
    val description: String,
    val scales: Map<String, Int>
)