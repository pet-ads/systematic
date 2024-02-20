package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class SearchSource(val id: String) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()
        val regex = Regex("([A-Z][A-Za-z]+)(\\s*[A-Za-z]+)*")

        if (id.isBlank())
            notification.addError("A search source must not be blank!")
        if (!(regex matches id))
            notification.addError("A search source must only contain words separated by white spaces. " +
                    "Provided: $id")

        return notification
    }
}
