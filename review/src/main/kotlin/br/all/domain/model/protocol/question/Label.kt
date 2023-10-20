package br.all.domain.model.protocol.question

import br.all.domain.shared.ddd.Notification

class Label(
    private val name: String,
    private val value: Int
) {

    //TODO sempre que for criar, tem que validar, senão não adianta. =D
    init {
        val notification = validate()
        require(notification.hasNoErrors()) {notification.message()}
    }

    //TODO: isBlank = isEmpty + contains only blank spaces. ;)
    //TODO: provide meaninfull messages (use the provided input as a hint)
    private fun validate(): Notification {
        val notification = Notification()
        if (name.isBlank()) notification.addError("Label name can not be black: \"$name\"")
        if (value < 0) notification.addError("Value must be a positive integer: $value")
        return notification
    }

    override fun toString() = "Label(name: $name, value: $value)"


}