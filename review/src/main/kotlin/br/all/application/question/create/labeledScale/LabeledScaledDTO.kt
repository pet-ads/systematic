package br.all.application.question.create.labeledScale

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.QuestionId
import java.util.UUID

data class LabeledScaledDTO(
    val questionId: UUID,
    val protocolId: UUID,
    val code: String,
    val description: String,
    val scales: Map<String, Int>
)
