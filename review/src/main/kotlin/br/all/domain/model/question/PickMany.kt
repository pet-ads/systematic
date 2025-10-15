package br.all.domain.model.question

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.Answer
import br.all.domain.shared.ddd.Notification

class PickMany(
    id: QuestionId,
    systematicStudyId: SystematicStudyId,
    code: String,
    description: String,
    val options: List<String>
) : Question<List<String>>(id, systematicStudyId, code, description) {

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

    override fun answer(value: List<String>): Answer<List<String>> {
        if (value.isEmpty()) throw IllegalArgumentException("Answer must not be empty.")
        value.forEach {
            if (it.isBlank()) throw IllegalArgumentException("Answer must not be blank.")
            if (it !in options) throw IllegalArgumentException("Answer must be one of the valid options: $options")
        }
        return Answer(id.value(), value)
    }

    override fun toString() =
        "PickMany(QuestionId: $id, ProtocolId: $systematicStudyId, Code: $code, " +
                "Description: $description, Options: $options)"
}