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
        val regex = Regex("([A-Z][A-Za-z]+)(\\s*[A-Za-z]+)*")

        if (searchSource.isBlank())
            notification.addError("A search source must not be blank!")
        if (!(regex matches searchSource))
            notification.addError("A search source must only contain words separated by white spaces. " +
                    "Provided: $searchSource")

        return notification
    }
}
