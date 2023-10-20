package br.all.domain.model.protocol.question

import br.all.domain.shared.ddd.Notification

class Label(
    private val name: String,
    private val value: Int
) {
    private fun validate(): Notification {
        val notification = Notification()
        if (name.isEmpty())
            notification.addError("Label name can not be empty.")
        if (value < 0)
            notification.addError("Value must be positive integer.")
        return notification
    }

    override fun toString() = "Label(name: $name, value: $value)"


}