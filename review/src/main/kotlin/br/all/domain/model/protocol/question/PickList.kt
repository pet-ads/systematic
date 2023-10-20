package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.shared.ddd.Notification

class PickList(
    id: QuestionId,
    protocolId: ProtocolId,
    code: String,
    description: String,
    private val options: List<String>
) : Question<String>(id, protocolId, code, description) {

    override fun validate(): Notification {
        val notification = super.validate()
        if (options.isEmpty()) {
            notification.addError("Can not create a picklist without a option to pick.")
        } else {
            options.forEachIndexed { index, option ->
                if (option.isBlank()) {
                    notification.addError("Option at index $index is empty or blank.")
                }
            }
        }

        return notification
    }

    override fun validateAnswer(value: String?) = options.first { it == value }
    override fun toString(): String {
        return "PickList(QuestionId: $id, ProtocolId: $protocolId, Code: $code, Description: $description, " +
                "Options: $options, Answer: $answer.)"
    }
}