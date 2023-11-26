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

        if (email.isEmpty()) {
            notification.addError("Email must not be empty.")
            return notification
        }
        if (email.isBlank()) {
            notification.addError("Email must not be blank.")
            return notification
        }
        if (!isValidEmailFormat(email)) notification.addError("Wrong Email format.")
        return notification
    }

    private fun isValidEmailFormat(email: String): Boolean {
        if (!isValidEmailAddress(email)) return false
        if (!HasLengthBelowMaximum(email)) return false
        if (hasRepeatedSubdomains(email)) return false
        if (email.contains("..")) return false
        if (email.contains("@.")) return false
        if (email.contains(".@")) return false
        return true
    }

    private fun isValidEmailAddress(email: String): Boolean {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[a-z.]+$")
        return regex.matches(email)
    }

    fun hasRepeatedSubdomains(email: String): Boolean {
        val parts = email.split("@")

        if (parts.size == 2) {
            val subdomains = parts[1].split(".")
            val verifyedSubdomains = HashSet<String>()

            for (subdomain in subdomains){
                if (!verifyedSubdomains.add(subdomain)) return true
            }
        }
        return false
    }

    fun HasLengthBelowMaximum(email: String): Boolean {
        val parts = email.split("@")
        return !(parts[0].length > 64 || parts[1].length > 255)
    }
}