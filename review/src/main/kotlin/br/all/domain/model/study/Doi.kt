package br.all.domain.model.study

import br.all.domain.common.ddd.Notification
import br.all.domain.common.ddd.ValueObject

data class Doi(val value: String) : ValueObject() {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate(): Notification {
        val notification = Notification()
        if (value.isBlank()) notification.addError("DOI must not be blank.")
        val regex = Regex("^10\\.\\d{4,}/[\\w\\.-]+$")
        if (!value.matches(regex)) notification.addError("Wrong DOI format: $value")
        return notification
    }
}