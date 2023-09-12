package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Identifier
import br.all.domain.shared.ddd.Notification
import java.util.UUID

@JvmInline
value class ProtocolId(val value : UUID) : Identifier {
    override fun validate() = Notification()

    override fun toString() = value.toString()
}