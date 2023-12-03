package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Identifier
import br.all.domain.shared.ddd.Notification
import java.util.*

@JvmInline
value class ProtocolId(val value: UUID) : Identifier<UUID> {
    override fun validate() = Notification()
    override fun value(): UUID = value
    override fun toString() = value.toString()
}