package br.all.domain.model.protocol.question

import br.all.domain.shared.ddd.Notification

data class Label(
    private val name: String,
    private val value: Int
) {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    private fun validate(): Notification {
        val notification = Notification()
        if (name.isBlank()) notification.addError("Label name must not be blank: \"$name\"")
        if (value < 0) notification.addError("Value must be a positive integer: $value")
        return notification
    }

    override fun toString() = "Label(name: $name, value: $value)"

    fun getName(): String{
        return name
    }

    fun getValue(): Int{
        return value
    }

}