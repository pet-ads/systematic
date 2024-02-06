package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class SearchSource(private val value: String) : ValueObject() {
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate() = Notification().also {
        val regex = Regex("([A-Z][A-Za-z]+)(\\s?[A-Za-z]+)*")

        if (value.isBlank())
            it.addError("A search source must not be blank!")
        if (!(regex matches value))
            it.addError(
                "A search source must only contain words separated by white spaces. Provided: $value"
            )
    }

    override fun toString() = value
}

fun String.toSearchSource() = SearchSource(this)
