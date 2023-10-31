package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class SearchSource(val searchSource: String) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()
        val regex = Regex("([A-Z][a-z]+)( [A-Z][A-Za-z]+)*")

        if (searchSource.isBlank())
            notification.addError("A search source must not be blank!")
        if (!(regex matches searchSource))
            notification.addError("A search source must contain words with only letters and that begin " +
                    "with a capital one! Provided: $searchSource")

        return notification
    }
}
