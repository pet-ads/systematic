package br.all.domain.shared.ddd

data class Email(val email: String) : ValueObject() {

    init {
        val notification = validate()

        require(notification.hasNoErrors()) {notification.message()}
    }
    override fun validate(): Notification {
        val notification = Notification()

        if (email.isEmpty()) notification.addError("Email must not be empty.")
        if (email.isBlank()) notification.addError("Email must not be blank.")
        if (!isValidEmailFormat(email)) notification.addError("Wrong Email format")

        return notification
    }

    private fun isValidEmailFormat(email: String): Boolean {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[a-z.]+$")
        return regex.matches(email)
    }

}