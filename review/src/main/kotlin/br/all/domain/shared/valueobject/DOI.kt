package br.all.domain.shared.valueobject

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class DOI(val value: String) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()

        if (value.isEmpty()) {
            notification.addError("DOI must not be empty.")
            return notification
        }
        if (value.isBlank()) {
            notification.addError("DOI must not be blank.")
            return notification
        }
        if (!isValidDOI(value)) {
            notification.addError("Wrong DOI format.")
            return notification
        }


        return notification
    }

    private fun isValidDOI(value: String): Boolean {
        val regex = Regex("^10\\.\\d{4,}/[A-Za-z0-9+_.~-]+\$")
        return regex.matches(value)
    }
}
