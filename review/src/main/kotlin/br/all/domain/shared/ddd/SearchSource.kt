package br.all.domain.shared.ddd

import javax.swing.Spring

data class SearchSource(val searchSource: String) : ValueObject() {

    init {
        val notification = validate()

        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()

        if (searchSource.isEmpty()) notification.addError("SearchSource must not be empty.")
        if (searchSource.isBlank()) notification.addError("SearchSource must not be blank.")

        return notification
    }

}
