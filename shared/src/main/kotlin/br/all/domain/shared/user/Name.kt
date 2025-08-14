package br.all.domain.shared.user

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Name(val value: String) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()

        if (value.isBlank()) {
            notification.addError("The name must not be blank!")
        } else {
            if (value.startsWith(" ") || value.endsWith(" ")) {
                notification.addError("The name must not start or end with blank spaces!")
            }

            if (!value.matches(Regex("[\\p{L}. ]+"))) {
                notification.addError("The name must contain only letters, dots, and blank spaces!")
            }
        }

        return notification
    }
}
