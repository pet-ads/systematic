package br.all.domain.model.protocol.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.shared.ddd.Entity
import br.all.domain.shared.ddd.Notification

abstract class Question<T>(
    questionId: QuestionId,
    val protocolId: ProtocolId,
    val code: String,
    val description: String,
) : Entity(questionId) {
    var answer: T? = null
        set(value) {
            field = validateAnswer(value)
        }

    abstract fun validateAnswer(value: T?): T


    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    protected open fun validate(): Notification {
        val notification = Notification()
        if (code.isBlank()) {
            notification.addError("Question code must not be blank. Provided: \"$code\"")
        }
        if (description.isBlank()) {
            notification.addError("Question description must not be blank. Provided: \"$description\"")
        }
        return notification
    }
}