package br.all.domain.shared.ddd

data class Text(val text: String) : ValueObject() {

    init {
        val notification = validate()

        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()

        if (text.isEmpty()) notification.addError("Text must not be empty.")
        if (text.isBlank()) notification.addError("Text must not be blank.")

        return notification
    }

}