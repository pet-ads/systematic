package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId

class QuestionBuilder private constructor(
    private var questionId: QuestionId,
    private var protocolId: ProtocolId,
    private var code: String,
    private var description: String,
) {
    companion object {
        fun with(
            id: QuestionId,
            protocolId: ProtocolId,
            code: String,
            description: String,
        ) = QuestionBuilder(id, protocolId, code, description)
    }

    fun buildTextual() = Textual(questionId, protocolId, code, description)

    fun buildLabeledScale(scales: Map<String, Int>) = LabeledScale(questionId, protocolId, code, description, scales)

    fun buildNumberScale(lower: Int, higher: Int) = NumberScale(questionId, protocolId, code, description, lower, higher)

    fun buildPickList(options: List<String>) = PickList(questionId, protocolId, code, description, options)
}


