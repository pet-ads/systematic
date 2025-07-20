package br.all.domain.user

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Username(val value: String) : ValueObject(){

    init {
        val notification = validate()
        require(notification.hasNoErrors()) {notification.message()}
    }

    override fun validate(): Notification {
        val notification = Notification()
        if (value.isBlank())
            notification.addError("Username must not be blank!")
        else if (!value.matches( Regex("[a-zA-Z0-9-_]*")))
            notification.addError("Username must contain only letters and numbers, dashes and underscores!")
        return notification
    }
}
