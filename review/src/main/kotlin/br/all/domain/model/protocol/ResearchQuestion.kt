package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class ResearchQuestion(private val description: String) : ValueObject() {
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate() = Notification().also {
        if (description.isBlank()) it.addError("The research question description must not be blank!")
    }

    override fun toString() = description
}
