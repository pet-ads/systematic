package br.all.application.question.create.pickList

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.question.QuestionId
import java.util.*

data class PickListRequestModel(
    val protocolId: ProtocolId,
    val code: String,
    val description: String,
    val options: List<String>
)