package br.all.domain.shared.utils

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Email(val email: String) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) {notification.message()}
    }
    override fun validate(): Notification {
        val notification = Notification()

        if (email.isEmpty()) notification.addError("Email must not be empty.")
        if (email.isBlank()) notification.addError("Email must not be blank.")
        if (!isValidEmailFormat(email)) notification.addError("Wrong Email format.")

        //TODO what is the min and max size of an email? Come on, you can be more psycho finding possible problems than that.
        return notification
    }

    private fun isValidEmailFormat(email: String): Boolean {
        //TODO I have added some failing tests for you. The regex is the right path, but the following is a but simplistic
        val regex = Regex("^[A-Za-z0-9+_.-]+@[a-z.]+$")
        return regex.matches(email)
    }
}