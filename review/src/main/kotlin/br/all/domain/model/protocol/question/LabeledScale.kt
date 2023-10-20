package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.shared.ddd.Notification

class LabeledScale(
    id: QuestionId,
    protocolId: ProtocolId,
    code: String,
    description: String,
    scales: Map<String, Int>
) : Question<Label>(id, protocolId, code, description) {

    private val _scales = scales.map{(key, value) -> Label(key, value)}
    override fun validate(): Notification {
        val notification = super.validate()

        if (_scales.isEmpty())
            notification.addError("Can not create a LabeledScale without Label to pick")
        return notification
    }

    override fun validateAnswer(value: Label?) = _scales.first { it == value }

    override fun toString() =
        "LabeledScale(QuestionId: $id, ProtocolId: $protocolId, Code: $code, Description: $description, " +
                "Scales: $_scales, Answer: $answer.)"

}