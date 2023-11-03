package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.shared.ddd.Notification

class NumberScale(
    id: QuestionId,
    protocolId: ProtocolId,
    code: String,
    description: String,
    val higher: Int,
    val lower: Int,
) : Question<Int>(id, protocolId, code, description) {

    override fun validate(): Notification {
        val notification = super.validate()
        if (higher < lower)
            notification.addError(
                "\"Higher\" value (${higher}) must be greater or equal than \"lower\" (${lower}) value."
            )
        return notification
    }

    override fun validateAnswer(value: Int?): Int {
        if (value == null)
            throw NullPointerException("Answer can not be null.")
        if (value > higher || value < lower)
            throw IllegalArgumentException("Answer is out of bounds [$lower, $higher]: $value")
        return value
    }

    operator fun component5(): Int = higher
    operator fun component6(): Int = lower

    override fun toString() = "NumberScale(questionId=$id, higher=$higher, lower=$lower)"
}