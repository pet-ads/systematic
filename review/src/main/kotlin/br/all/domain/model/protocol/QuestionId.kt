package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Identifier
import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject

data class QuestionId(val id: Int) : Identifier {

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