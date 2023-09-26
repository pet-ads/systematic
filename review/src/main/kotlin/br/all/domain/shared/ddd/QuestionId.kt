package br.all.domain.shared.ddd

data class QuestionId(val id: Int) : ValueObject() {

    init {
        val notification = validate()

        require(notification.hasNoErrors()) {notification.message()}
    }
    override fun validate(): Notification {
        val notification = Notification()

        if (id <= 0) notification.addError("ID must be greater than zero.")

        return notification
    }


}