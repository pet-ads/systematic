package br.all.domain.model.search

import br.all.domain.shared.ddd.Identifier
import br.all.domain.shared.ddd.Notification
import java.util.*

@JvmInline
value class SearchSessionID(val value: UUID) : Identifier<UUID> {

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate() = Notification()
    override fun value(): UUID = value
    override fun toString(): String = value.toString()
}

fun UUID.toSearchSessionID() = SearchSessionID(this)