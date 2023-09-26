package br.all.domain.shared.utils

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Text(val text: String) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()

        if (text.isEmpty()) notification.addError("Text must not be empty.")
        if (text.isBlank()) notification.addError("Text must not be blank.")

        //TODO What is not a Text? @@@ is a text? Regex baby!

        return notification
    }

}