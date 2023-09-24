package br.all.domain.model.question

import br.all.domain.shared.ddd.Identifier
import br.all.domain.shared.ddd.Notification

class QuestionId(private val  value: Long) : Identifier {
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()
        if (value <= 0) notification.addError("Question must be positive non-zero. Provided: $value")
        return notification
    }

    override fun toString() = value.toString()
}

