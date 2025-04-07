package br.all.domain.model.collaboration

import br.all.domain.shared.ddd.Identifier
import br.all.domain.shared.ddd.Notification
import java.util.*

@JvmInline
value class CollaborationId(private val value: UUID) : Identifier<UUID> {
    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    override fun validate() = Notification()

    override fun value() = value

    override fun toString() = value.toString()
}

fun UUID.toCollaborationId() = CollaborationId(this)