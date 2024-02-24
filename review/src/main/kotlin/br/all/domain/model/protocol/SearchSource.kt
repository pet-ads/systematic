package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class SearchSource(private var value: String) : ValueObject() {

    init {
        value = value.replace("(^|\\s)[a-z]".toRegex()) { it.value.uppercase() }
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate() = Notification().also {
        val regex = Regex("([a-z]+)(\\s[a-z]+)*", RegexOption.IGNORE_CASE)

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
