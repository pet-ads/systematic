package br.all.application.protocol.question.create.labeledScale

import java.util.UUID

data class LabeledScaledDTO(
    val questionId: UUID,
    val protocolId: UUID,
    val code: String,
    val description: String,
    val scales: Map<String, Int>
)
