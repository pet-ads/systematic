package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.shared.ddd.Notification

class PickList(
    id: QuestionId,
    protocolId: ProtocolId,
    code: String,
    description: String,
    val options: List<String>
) : Question<String>(id, protocolId, code, description) {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = super.validate()
        if (options.isEmpty())
            notification.addError("Can not create a picklist without a option to pick.")

        options.forEachIndexed { index, option ->
            if (option.isBlank()) notification.addError("Option at index $index is empty or blank.")
        }
        return notification
    }

    override fun validateAnswer(value: String?): String {
        if (value == null) throw NullPointerException("Answer must not be null.")
        if (value.isBlank()) throw IllegalArgumentException("Answer must not be blank.")
        if (value !in options) throw IllegalArgumentException("Answer must be one of the valid options: $options")
        return value
    }

    override fun toString() =
        "PickList(QuestionId: $id, ProtocolId: $protocolId, Code: $code, " +
                "Description: $description, Options: $options, Answer: $answer.)"
}