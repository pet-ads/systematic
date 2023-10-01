package br.all.domain.model.search

import br.all.domain.shared.ddd.Identifier
import br.all.domain.shared.ddd.Notification
import java.util.UUID

@JvmInline
value class SearchSessionID(val value: UUID) : Identifier {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    //TODO you don't need a body if a function has online one line
    override fun validate() = Notification()

    override fun toString(): String = value.toString()
}