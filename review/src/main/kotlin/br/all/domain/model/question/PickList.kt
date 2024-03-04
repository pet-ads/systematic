package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.Answer
import br.all.domain.shared.ddd.Notification

class PickList(
    id: QuestionId,
    systematicStudyId: SystematicStudyId,
    code: String,
    description: String,
    val options: List<String>
) : Question<String>(id, systematicStudyId, code, description) {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    fun validate(): Notification {
        val notification = Notification()
        if (options.isEmpty())
            notification.addError("Can not create a picklist without a option to pick.")

        options.forEachIndexed { index, option ->
            if (option.isBlank()) notification.addError("Option at index $index is empty or blank.")
        }
        return notification
    }

    override fun answer(value: String): Answer<String> {
        if (value.isBlank()) throw IllegalArgumentException("Answer must not be blank.")
        if (value !in options) throw IllegalArgumentException("Answer must be one of the valid options: $options")
        return Answer(id.value(), value)
    }

    override fun toString() =
        "PickList(QuestionId: $id, ProtocolId: $systematicStudyId, Code: $code, " +
                "Description: $description, Options: $options)"
}