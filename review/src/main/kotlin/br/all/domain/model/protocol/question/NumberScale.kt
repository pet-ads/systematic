package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.shared.ddd.Notification

class NumberScale(
    id: QuestionId,
    protocolId: ProtocolId,
    code: String,
    description: String,
    private val higher: Int,
    private val lower: Int,
) : Question<Int>(id, protocolId, code, description) {

    override fun validateAnswer(value: Int?): Int {
        if (value == null)
            throw NullPointerException("Answer can not be null.")
        if (value > higher || value < lower)
            throw IllegalArgumentException("Answer is out of bounds [$lower, $higher]: $value")
        return value
    }

    override fun validate(): Notification {
        val notification = super.validate()
        if (higher < lower)
            notification.addError("\"Higher\" ${higher} value must be greater or equal than \"lower\" value.")
        return notification
    }

    override fun toString() = "NumberScale(questionId=$id, higher=$higher, lower=$lower)"
}