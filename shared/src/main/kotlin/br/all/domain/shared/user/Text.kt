package br.all.domain.shared.user

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Text(val value: String) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()

        if (value.isBlank())
            notification.addError("The text must not be blank!")
        else {
            if (value.startsWith(" ") || value.endsWith(" "))
                notification.addError("The text must not start or end with blank spaces!")
            if (!value.matches(Regex("[a-zA-Z ]+")))
                notification.addError("The text must contain only letters and blank spaces!")
        }
        return notification;
    }
}