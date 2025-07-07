package br.all.domain.user

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class Email(val value: String) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()

        if (value.isEmpty()) {
            notification.addError("Email must not be empty.")
            return notification
        }

        if (value.isBlank()) {
            notification.addError("Email must not be blank.")
            return notification
        }

        if (!isValidEmailFormat(value)) notification.addError("Wrong Email format.")

        return notification
    }

    private fun isValidEmailFormat(email: String): Boolean {
        if (email.contains("..") || email.contains(".@") || email.contains("@.")) return false
        if (email.startsWith(".") || email.endsWith(".")) return false
        if (email.startsWith("@") || email.endsWith("@")) return false

        val parts = email.split("@")
        if (parts.size != 2) return false

        val localPart = parts[0]
        val domainPart = parts[1]

        if (!hasValidLength(localPart, domainPart)) return false
        if (!isValidStructure(localPart, domainPart)) return false

        return true
    }

    private fun isValidStructure(localPart: String, domainPart: String): Boolean {
        val localRegex = Regex("^[A-Za-z0-9_!#$%&'*+/=?`{|}~^.-]+$")
        val domainRegex = Regex("^[A-Za-z0-9.-]+$")

        val domainLabels = domainPart.split(".")
        if (domainLabels.last().length < 2 || domainLabels.any { it.startsWith("-") || it.endsWith("-") }) {
            return false
        }

        return localRegex.matches(localPart) && domainRegex.matches(domainPart)
    }

    private fun hasValidLength(localPart: String, domainPart: String): Boolean {
        if (localPart.length > 64) return false
        if (domainPart.length > 255) return false
        if ((localPart.length + 1 + domainPart.length) > 254) return false
        return true
    }
}