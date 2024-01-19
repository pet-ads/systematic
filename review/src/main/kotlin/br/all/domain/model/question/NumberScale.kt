package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.study.Answer
import br.all.domain.shared.ddd.Notification

class NumberScale(
    id: QuestionId,
    protocolId: ProtocolId,
    code: String,
    description: String,
    val higher: Int,
    val lower: Int,
) : Question<Int>(id, protocolId, code, description) {
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    fun validate(): Notification {
        val notification = Notification()
        if (higher < lower)
            notification.addError(
                "\"Higher\" value (${higher}) must be greater or equal than \"lower\" (${lower}) value."
            )
        return notification
    }

    override fun answer(value: Int): Answer<Int> {
        if (value > higher || value < lower)
            throw IllegalArgumentException("Answer is out of bounds [$lower, $higher]: $value")
        return Answer(id.value(), value)
    }

    operator fun component5(): Int = higher
    operator fun component6(): Int = lower

    override fun toString() = "NumberScale(questionId=$id, higher=$higher, lower=$lower)"
}